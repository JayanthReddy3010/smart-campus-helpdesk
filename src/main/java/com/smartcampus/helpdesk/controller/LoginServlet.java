package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.UserDAO;
import com.smartcampus.helpdesk.model.User;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = value(request.getParameter("email"));
        String password = request.getParameter("password");

        if (email.isBlank() || password == null || password.isBlank()) {
            showError(request, response, "Enter both email and password.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email.toLowerCase(Locale.ROOT));
            if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                showError(request, response, "Invalid email or password.");
                return;
            }

            AuthUtil.signIn(request, user);
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (SQLException | IllegalStateException exception) {
            showError(request, response, "Login is temporarily unavailable. Please try again later.");
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}
