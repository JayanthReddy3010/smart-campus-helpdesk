<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.smartcampus.helpdesk.model.Ticket" %>
<%@ page import="com.smartcampus.helpdesk.model.TicketComment" %>
<%@ page import="com.smartcampus.helpdesk.model.TicketHistory" %>
<%@ page import="com.smartcampus.helpdesk.model.TicketStatus" %>
<%@ page import="com.smartcampus.helpdesk.model.User" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%@ page import="com.smartcampus.helpdesk.util.TicketStatusWorkflow" %>
<%
    Ticket ticket = (Ticket) request.getAttribute("ticket");
    List<TicketComment> comments = (List<TicketComment>) request.getAttribute("comments");
    List<TicketHistory> history = (List<TicketHistory>) request.getAttribute("history");
    List<User> staffUsers = (List<User>) request.getAttribute("staffUsers");
        java.util.Set<TicketStatus> nextStatuses = ticket == null
            ? java.util.Collections.emptySet()
            : TicketStatusWorkflow.allowedNextStatuses(ticket.getStatus());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ticket Details | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="brand"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
        <nav class="nav-menu" aria-label="Admin navigation">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Admin overview</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/tickets">All tickets</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/category-xml">Category XML</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="nav-link nav-button" type="submit">Log out</button></form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div><p class="eyebrow">Administration / Ticket details</p><h1><%= ticket == null ? "Unavailable" : "#" + ticket.getTicketId() %></h1></div>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/admin/tickets">Back to all tickets</a>
        </header>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div>
        <% } %>
        <% if (ticket != null) { %>
            <% if ("true".equals(request.getParameter("assigned"))) { %>
                <div class="success" role="status">Ticket assignment saved and history updated.</div>
            <% } %>
            <% if (request.getAttribute("statusMessage") != null) { %>
                <div class="success" role="status"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("statusMessage"))) %></div>
            <% } %>
            <section class="detail-header panel">
                <div><p class="eyebrow">Request subject</p><h2><%= HtmlUtil.escape(ticket.getSubject()) %></h2><p class="muted">Submitted <%= ticket.getCreatedAt() == null ? "-" : HtmlUtil.escape(ticket.getCreatedAt().toString().replace('T', ' ')) %></p></div>
                <div class="detail-badges"><span class="status status-<%= ticket.getStatus().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getStatus().name().replace('_', ' ')) %></span><span class="priority priority-<%= ticket.getPriority().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getPriority().name()) %></span></div>
            </section>
            <section class="detail-grid">
                <article class="panel detail-card"><p class="eyebrow">Description</p><p class="detail-copy"><%= HtmlUtil.escape(ticket.getDescription()) %></p></article>
                <article class="panel detail-card"><p class="eyebrow">Request information</p><dl class="info-list"><dt>Location</dt><dd><%= HtmlUtil.escape(ticket.getLocation()) %></dd><dt>Assigned staff ID</dt><dd><%= ticket.getAssignedTo() == null ? "Unassigned" : ticket.getAssignedTo() %></dd><dt>Last updated</dt><dd><%= ticket.getUpdatedAt() == null ? "-" : HtmlUtil.escape(ticket.getUpdatedAt().toString().replace('T', ' ')) %></dd><dt>Resolved</dt><dd><%= ticket.getResolvedAt() == null ? "Not resolved" : HtmlUtil.escape(ticket.getResolvedAt().toString().replace('T', ' ')) %></dd></dl></article>
            </section>
            <% if (ticket.getResolution() != null && !ticket.getResolution().isBlank()) { %>
                <section class="panel detail-card"><p class="eyebrow">Resolution</p><p class="detail-copy"><%= HtmlUtil.escape(ticket.getResolution()) %></p></section>
            <% } %>
            <section class="detail-grid admin-assignment-grid">
                <article class="panel detail-card">
                    <p class="eyebrow">Assignment</p>
                    <form method="post" action="${pageContext.request.contextPath}/admin/assign-ticket" data-confirm="Assign this ticket to the selected staff member?">
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                        <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
                        <label for="staffId">Support staff</label>
                        <select id="staffId" name="staffId" required>
                            <option value="">Select staff member</option>
                            <% if (staffUsers != null) { for (User staff : staffUsers) { %>
                                <option value="<%= staff.getUserId() %>" <%= ticket.getAssignedTo() != null && ticket.getAssignedTo().equals(staff.getUserId()) ? "selected" : "" %>><%= HtmlUtil.escape(staff.getName()) %> - <%= HtmlUtil.escape(staff.getDepartment()) %></option>
                            <% } } %>
                        </select>
                        <button class="button button-primary" type="submit">Assign ticket</button>
                    </form>
                </article>
                <article class="panel detail-card">
                    <p class="eyebrow">History</p>
                    <% if (history == null || history.isEmpty()) { %>
                        <p class="muted">No history entries recorded.</p>
                    <% } else { %>
                        <ol class="history-list">
                        <% for (TicketHistory entry : history) { %>
                            <li><strong><%= HtmlUtil.escape(entry.getActionDescription()) %></strong><span><%= entry.getOldStatus() == null ? "Created" : HtmlUtil.escape(entry.getOldStatus().name().replace('_', ' ')) %> &rarr; <%= HtmlUtil.escape(entry.getNewStatus().name().replace('_', ' ')) %></span><small><%= entry.getChangedAt() == null ? "-" : HtmlUtil.escape(entry.getChangedAt().toString().replace('T', ' ')) %></small></li>
                        <% } %>
                        </ol>
                    <% } %>
                </article>
            </section>
            <section class="panel detail-card comments-card">
                <p class="eyebrow">Comments</p>
                <% if (comments == null || comments.isEmpty()) { %>
                    <p class="muted">No comments yet.</p>
                <% } else { %>
                    <div class="comments-list">
                    <% for (TicketComment entry : comments) { %>
                        <div class="comment-entry"><p><%= HtmlUtil.escape(entry.getComment()) %></p><small><%= entry.getCreatedAt() == null ? "-" : HtmlUtil.escape(entry.getCreatedAt().toString().replace('T', ' ')) %></small></div>
                    <% } %>
                    </div>
                <% } %>
                <form method="post" action="${pageContext.request.contextPath}/admin/add-comment" class="comment-form" data-comment-form novalidate>
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                    <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
                    <label for="comment">Add a comment</label>
                    <textarea id="comment" name="comment" rows="4" maxlength="2000" required placeholder="Write an internal update..." data-comment-input></textarea>
                    <button class="button button-primary" type="submit">Add comment</button>
                </form>
            </section>
            <% if (!nextStatuses.isEmpty()) { %>
                <section class="panel detail-card status-update-card">
                    <p class="eyebrow">Status workflow</p>
                    <p class="muted">Only the next valid workflow step is available.</p>
                    <form method="post" action="${pageContext.request.contextPath}/admin/update-status" class="status-update-form" data-confirm="Update this ticket status?">
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                        <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
                        <label for="newStatus">Move ticket to</label>
                        <select id="newStatus" name="newStatus" required>
                            <% for (TicketStatus nextStatus : nextStatuses) { %>
                                <option value="<%= nextStatus.name() %>"><%= HtmlUtil.escape(nextStatus.name().replace('_', ' ')) %></option>
                            <% } %>
                        </select>
                        <button class="button button-primary" type="submit">Update status</button>
                    </form>
                </section>
            <% } %>
        <% } %>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
