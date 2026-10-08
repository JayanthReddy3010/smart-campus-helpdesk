package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/student/dashboard", "/staff/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = AuthUtil.getUserId(request);
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            request.setAttribute("totalTickets", ticketDAO.countByUserId(userId));
            request.setAttribute("openTickets", ticketDAO.countByUserId(userId, TicketStatus.OPEN));
            request.setAttribute("inProgressTickets", ticketDAO.countByUserId(userId, TicketStatus.IN_PROGRESS));
            request.setAttribute("resolvedTickets", ticketDAO.countByUserId(userId, TicketStatus.RESOLVED));
            request.setAttribute("recentTickets", ticketDAO.findRecentByUserId(userId, 5));
            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Ticket information is temporarily unavailable.");
            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        }
    }
}
