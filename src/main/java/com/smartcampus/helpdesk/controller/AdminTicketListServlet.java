package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.CategoryDAO;
import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.model.TicketPriority;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.dao.UserDAO;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/tickets")
public class AdminTicketListServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AuthUtil.getRole(request) != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        TicketStatus status = parseStatus(request.getParameter("status"));
        TicketPriority priority = parsePriority(request.getParameter("priority"));
        Integer categoryId = parseCategory(request.getParameter("category"));
        Integer assignedStaffId = parseCategory(request.getParameter("assignedStaff"));
        String search = request.getParameter("search");
        if (search != null && search.length() > 100) {
            search = search.substring(0, 100);
        }

        try {
            List<Category> categories = categoryDAO.findAll();
            List<User> staffUsers = userDAO.findByRole(UserRole.STAFF);
            request.setAttribute("categories", categories);
            request.setAttribute("staffUsers", staffUsers);
            request.setAttribute("tickets", ticketDAO.findAdminTickets(status, priority, categoryId, search, assignedStaffId));
            request.setAttribute("selectedStatus", status == null ? "" : status.name());
            request.setAttribute("selectedPriority", priority == null ? "" : priority.name());
            request.setAttribute("selectedCategory", categoryId == null ? "" : categoryId.toString());
            request.setAttribute("selectedAssignedStaff", assignedStaffId == null ? "" : assignedStaffId.toString());
            request.setAttribute("search", search == null ? "" : search);
            request.getRequestDispatcher("/admin/tickets.jsp").forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Ticket data is temporarily unavailable.");
            request.getRequestDispatcher("/admin/tickets.jsp").forward(request, response);
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
