package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.CategoryDAO;
import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.model.TicketPriority;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/student/tickets", "/staff/tickets"})
public class MyTicketsServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = AuthUtil.getUserId(request);
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        TicketStatus status = parseStatus(request.getParameter("status"));
        TicketPriority priority = parsePriority(request.getParameter("priority"));
        Integer categoryId = parseCategory(request.getParameter("category"));
        String search = request.getParameter("search");
        if (search != null && search.length() > 100) {
            search = search.substring(0, 100);
        }

        try {
            List<Category> categories = categoryDAO.findAll();
            request.setAttribute("categories", categories);
            request.setAttribute("tickets", ticketDAO.findByUserIdFiltered(userId, status, priority, categoryId, search));
            request.setAttribute("selectedStatus", status == null ? "" : status.name());
            request.setAttribute("selectedPriority", priority == null ? "" : priority.name());
            request.setAttribute("selectedCategory", categoryId == null ? "" : categoryId.toString());
            request.setAttribute("search", search == null ? "" : search);
            request.getRequestDispatcher("/student/my-tickets.jsp").forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Your tickets are temporarily unavailable.");
            request.getRequestDispatcher("/student/my-tickets.jsp").forward(request, response);
        }
    }

    private TicketStatus parseStatus(String value) {
        try {
            return value == null || value.isBlank() ? null : TicketStatus.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private TicketPriority parsePriority(String value) {
        try {
            return value == null || value.isBlank() ? null : TicketPriority.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private Integer parseCategory(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
