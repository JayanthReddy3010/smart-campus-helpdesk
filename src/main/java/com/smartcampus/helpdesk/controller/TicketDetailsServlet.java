package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.dao.TicketCommentDAO;
import com.smartcampus.helpdesk.dao.TicketHistoryDAO;
import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/student/ticket", "/staff/ticket"})
public class TicketDetailsServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final TicketCommentDAO commentDAO = new TicketCommentDAO();
    private final TicketHistoryDAO historyDAO = new TicketHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = AuthUtil.getUserId(request);
        Integer ticketId = parseId(request.getParameter("id"));
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (ticketId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Ticket ticket = ticketDAO.findByIdForUser(ticketId, userId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("ticket", ticket);
            request.setAttribute("comments", commentDAO.findByTicketId(ticketId));
            request.setAttribute("history", historyDAO.findByTicketId(ticketId));
            if ("true".equals(request.getParameter("commentAdded"))) {
                request.setAttribute("commentMessage", "Comment added successfully.");
            } else if ("invalid".equals(request.getParameter("commentError"))) {
                request.setAttribute("error", "Enter a comment of 2,000 characters or fewer.");
            } else if ("unavailable".equals(request.getParameter("commentError"))) {
                request.setAttribute("error", "The comment could not be added. Please try again.");
            }
            request.getRequestDispatcher("/student/ticket-details.jsp").forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Ticket details are temporarily unavailable.");
            request.getRequestDispatcher("/student/ticket-details.jsp").forward(request, response);
        }
    }

    private Integer parseId(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
