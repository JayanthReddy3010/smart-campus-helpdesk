package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.dao.TicketHistoryDAO;
import com.smartcampus.helpdesk.dao.UserDAO;
import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/assign-ticket")
public class AdminAssignmentServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final TicketHistoryDAO historyDAO = new TicketHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AuthUtil.getRole(request) != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer ticketId = parseId(request.getParameter("ticketId"));
        Integer staffId = parseId(request.getParameter("staffId"));
        Integer adminId = AuthUtil.getUserId(request);
        if (ticketId == null || staffId == null || adminId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Ticket ticket = ticketDAO.findById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            User staff = userDAO.findById(staffId);
            if (staff == null || staff.getRole() != UserRole.STAFF) {
                showError(request, response, ticket, "Select an existing staff member with a support role.");
                return;
            }

            if (!ticketDAO.assignToStaff(ticketId, staffId, adminId)) {
                showError(request, response, ticket, "The ticket could not be assigned.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/ticket?id=" + ticketId + "&assigned=true");
        } catch (SQLException exception) {
            showError(request, response, null, "The assignment could not be completed. Please try again.");
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response,
                           Ticket ticket, String message) throws ServletException, IOException {
        request.setAttribute("ticket", ticket);
        request.setAttribute("error", message);
        if (ticket != null) {
            try {
                request.setAttribute("history", historyDAO.findByTicketId(ticket.getTicketId()));
                request.setAttribute("staffUsers", userDAO.findByRole(UserRole.STAFF));
            } catch (SQLException exception) {
                request.setAttribute("error", "The ticket could not be loaded. Please try again.");
            }
        }
        request.getRequestDispatcher("/admin/ticket-details.jsp").forward(request, response);
    }

    private Integer parseId(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
