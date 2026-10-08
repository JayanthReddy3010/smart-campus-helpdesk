package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.TicketStatus;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/update-status")
public class AdminStatusUpdateServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (AuthUtil.getRole(request) != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer ticketId = parseId(request.getParameter("ticketId"));
        TicketStatus nextStatus = parseStatus(request.getParameter("newStatus"));
        Integer adminId = AuthUtil.getUserId(request);
        if (ticketId == null || nextStatus == null || adminId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (!ticketDAO.updateStatusWithHistory(ticketId, nextStatus, adminId,
                    "Ticket status changed to " + nextStatus.name())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/ticket?id=" + ticketId + "&statusUpdated=true");
        } catch (IllegalStateException exception) {
            response.sendRedirect(request.getContextPath() + "/admin/ticket?id=" + ticketId + "&statusError=invalid");
        } catch (SQLException exception) {
            response.sendRedirect(request.getContextPath() + "/admin/ticket?id=" + ticketId + "&statusError=unavailable");
        }
    }

    private Integer parseId(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private TicketStatus parseStatus(String value) {
        try {
            return value == null ? null : TicketStatus.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
