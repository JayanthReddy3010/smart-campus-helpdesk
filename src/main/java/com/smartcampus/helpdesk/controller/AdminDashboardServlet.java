package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AuthUtil.getRole(request) != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            request.setAttribute("totalTickets", ticketDAO.countAdminTickets());
            request.setAttribute("openTickets", ticketDAO.countAdminTickets(TicketStatus.OPEN));
            request.setAttribute("assignedTickets", ticketDAO.countAssignedAdminTickets());
            request.setAttribute("inProgressTickets", ticketDAO.countAdminTickets(TicketStatus.IN_PROGRESS));
            request.setAttribute("resolvedTickets", ticketDAO.countAdminTickets(TicketStatus.RESOLVED));
            request.setAttribute("closedTickets", ticketDAO.countAdminTickets(TicketStatus.CLOSED));
            request.setAttribute("highPriorityTickets", ticketDAO.countHighPriorityAdminTickets());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Administrative ticket data is temporarily unavailable.");
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
