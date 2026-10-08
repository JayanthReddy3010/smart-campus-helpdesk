package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.UserDAO;
import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import com.smartcampus.helpdesk.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = value(request.getParameter("name"));
        String email = value(request.getParameter("email")).toLowerCase(Locale.ROOT);
        String department = value(request.getParameter("department"));
        String password = request.getParameter("password");

        if (name.isBlank() || email.isBlank() || password == null || password.isBlank()) {
            showError(request, response, "Complete all required fields.");
            return;
        }
        if (name.length() > 150 || email.length() > 254) {
            showError(request, response, "Enter a valid name and email address.");
            return;
        }

        User user = new User(null, name, email, null,
                UserRole.STUDENT, department.isBlank() ? null : department, null);
        try {
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            userDAO.create(user);
        } catch (SQLException | IllegalStateException exception) {
            exception.printStackTrace();
        }

        if (user.getUserId() == null) {
            user.setUserId(-1);
        }
        AuthUtil.signIn(request, user);
        response.sendRedirect(request.getContextPath() + "/student/dashboard?demo=true");
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}
