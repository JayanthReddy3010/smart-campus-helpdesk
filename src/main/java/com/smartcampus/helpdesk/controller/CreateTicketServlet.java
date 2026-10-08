package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.CategoryDAO;
import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.model.TicketPriority;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import com.smartcampus.helpdesk.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet({"/student/create-ticket", "/staff/create-ticket"})
public class CreateTicketServlet extends HttpServlet {

    private static final String FORM_TOKEN = "createTicket.formToken";
    private static final int MAX_DESCRIPTION_LENGTH = 5000;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AuthUtil.getUserId(request) == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            request.setAttribute("categories", categoryDAO.findActive());
            request.getSession(true).setAttribute(FORM_TOKEN, UUID.randomUUID().toString());
            request.getRequestDispatcher("/student/create-ticket.jsp").forward(request, response);
        } catch (SQLException exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ticket categories are temporarily unavailable.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer userId = AuthUtil.getUserId(request);
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String categoryValue = value(request.getParameter("categoryId"));
        String subject = value(request.getParameter("subject"));
        String description = value(request.getParameter("description"));
        String location = value(request.getParameter("location"));
        String priorityValue = value(request.getParameter("priority"));
        preserveForm(request, categoryValue, subject, description, location, priorityValue);

        String validationError = validate(categoryValue, subject, description, location, priorityValue);
        if (validationError != null) {
            showForm(request, response, validationError);
            return;
        }

        Integer categoryId = Integer.valueOf(categoryValue);
        TicketPriority priority = TicketPriority.valueOf(priorityValue);
        try {
            Category category = categoryDAO.findById(categoryId);
            if (category == null || !category.isActive()) {
                showForm(request, response, "Choose an active category.");
                return;
            }

            HttpSession session = request.getSession(false);
            String expectedToken = session == null ? null : (String) session.getAttribute(FORM_TOKEN);
            String submittedToken = request.getParameter("formToken");
            if (expectedToken == null || submittedToken == null || !expectedToken.equals(submittedToken)) {
                showForm(request, response, "This form has expired. Please open a new ticket form.");
                return;
            }

            Ticket ticket = new Ticket(null, userId, categoryId, subject, description, location,
                    priority, null, null, null, null, null, null);
            ticketDAO.createWithInitialHistory(ticket, userId);
            session.removeAttribute(FORM_TOKEN);

            String area = AuthUtil.getRole(request) == UserRole.STUDENT ? "student" : "staff";
            response.sendRedirect(request.getContextPath() + "/" + area + "/ticket?id=" + ticket.getTicketId() + "&created=true");
        } catch (SQLException | IllegalStateException exception) {
            showForm(request, response, "The ticket could not be submitted. Please try again.");
        }
    }

    private String validate(String category, String subject, String description,
                            String location, String priority) {
        try {
            if (Integer.parseInt(category) <= 0) {
                return "Choose a category.";
            }
        } catch (NumberFormatException exception) {
            return "Choose a valid category.";
        }
        if (subject.isBlank() || subject.length() > 200) {
            return "Subject is required and must be 200 characters or fewer.";
        }
        if (description.isBlank() || description.length() > MAX_DESCRIPTION_LENGTH) {
            return "Description is required and must be 5,000 characters or fewer.";
        }
        if (location.isBlank() || location.length() > 255) {
            return "Location is required and must be 255 characters or fewer.";
        }
        try {
            TicketPriority.valueOf(priority);
        } catch (IllegalArgumentException exception) {
            return "Choose a valid priority.";
        }
        return null;
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        try {
            List<Category> categories = categoryDAO.findActive();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/student/create-ticket.jsp").forward(request, response);
        } catch (SQLException exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ticket categories are temporarily unavailable.");
        }
    }

    private void preserveForm(HttpServletRequest request, String category, String subject,
                              String description, String location, String priority) {
        request.setAttribute("formCategoryId", HtmlUtil.escape(category));
        request.setAttribute("formSubject", HtmlUtil.escape(subject));
        request.setAttribute("formDescription", HtmlUtil.escape(description));
        request.setAttribute("formLocation", HtmlUtil.escape(location));
        request.setAttribute("formPriority", HtmlUtil.escape(priority));
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}
