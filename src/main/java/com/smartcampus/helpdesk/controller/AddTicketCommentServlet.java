package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.TicketCommentDAO;
import com.smartcampus.helpdesk.dao.TicketDAO;
import com.smartcampus.helpdesk.model.Ticket;
import com.smartcampus.helpdesk.model.TicketComment;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/student/add-comment", "/staff/add-comment", "/admin/add-comment"})
public class AddTicketCommentServlet extends HttpServlet {

    private static final int MAX_COMMENT_LENGTH = 2000;

    private final TicketDAO ticketDAO = new TicketDAO();
    private final TicketCommentDAO commentDAO = new TicketCommentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Integer userId = AuthUtil.getUserId(request);
        Integer ticketId = parseId(request.getParameter("ticketId"));
        String comment = value(request.getParameter("comment"));
        UserRole role = AuthUtil.getRole(request);
        String area = role == UserRole.ADMIN ? "admin" : (role == UserRole.STAFF ? "staff" : "student");

        if (userId == null || role == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (ticketId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (comment.isBlank() || comment.length() > MAX_COMMENT_LENGTH) {
            redirectError(request, response, area, ticketId, "invalid");
            return;
        }

        try {
            Ticket ticket = role == UserRole.ADMIN
                    ? ticketDAO.findById(ticketId)
                    : ticketDAO.findByIdForUser(ticketId, userId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            commentDAO.create(new TicketComment(null, ticketId, userId, comment, null));
            response.sendRedirect(request.getContextPath() + "/" + area + "/ticket?id=" + ticketId + "&commentAdded=true");
        } catch (SQLException exception) {
            redirectError(request, response, area, ticketId, "unavailable");
        }
    }

    private void redirectError(HttpServletRequest request, HttpServletResponse response,
                               String area, Integer ticketId, String error) throws IOException {
        response.sendRedirect(request.getContextPath() + "/" + area + "/ticket?id=" + ticketId + "&commentError=" + error);
    }

    private Integer parseId(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}
