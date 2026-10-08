<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.smartcampus.helpdesk.model.Ticket" %>
<%@ page import="com.smartcampus.helpdesk.model.TicketComment" %>
<%@ page import="com.smartcampus.helpdesk.model.TicketHistory" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%
    String role = String.valueOf(session.getAttribute("auth.userRole"));
    String area = "STAFF".equals(role) || "ADMIN".equals(role) ? "staff" : "student";
    Ticket ticket = (Ticket) request.getAttribute("ticket");
    List<TicketComment> comments = (List<TicketComment>) request.getAttribute("comments");
    List<TicketHistory> history = (List<TicketHistory>) request.getAttribute("history");
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
        <nav class="nav-menu" aria-label="Main navigation">
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/create-ticket">Create ticket</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/<%= area %>/tickets">My tickets</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="nav-link nav-button" type="submit">Log out</button></form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div><p class="eyebrow">Ticket details</p><h1><%= ticket == null ? "Unavailable" : "#" + ticket.getTicketId() %></h1></div>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/<%= area %>/tickets">Back to tickets</a>
        </header>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div>
        <% } %>
        <% if (ticket != null) { %>
            <% if ("true".equals(request.getParameter("created"))) { %>
                <div class="success" role="status">Ticket <strong>#<%= ticket.getTicketId() %></strong> was submitted successfully.</div>
            <% } %>
            <% if (request.getAttribute("commentMessage") != null) { %>
                <div class="success" role="status"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("commentMessage"))) %></div>
            <% } %>
            <section class="detail-header panel">
                <div><p class="eyebrow">Request subject</p><h2><%= HtmlUtil.escape(ticket.getSubject()) %></h2><p class="muted">Submitted <%= ticket.getCreatedAt() == null ? "-" : HtmlUtil.escape(ticket.getCreatedAt().toString().replace('T', ' ')) %></p></div>
                <div class="detail-badges"><span class="status status-<%= ticket.getStatus().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getStatus().name().replace('_', ' ')) %></span><span class="priority priority-<%= ticket.getPriority().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getPriority().name()) %></span></div>
            </section>
            <section class="detail-grid">
                <article class="panel detail-card"><p class="eyebrow">Description</p><p class="detail-copy"><%= HtmlUtil.escape(ticket.getDescription()) %></p></article>
                <article class="panel detail-card"><p class="eyebrow">Request information</p><dl class="info-list"><dt>Location</dt><dd><%= HtmlUtil.escape(ticket.getLocation()) %></dd><dt>Last updated</dt><dd><%= ticket.getUpdatedAt() == null ? "-" : HtmlUtil.escape(ticket.getUpdatedAt().toString().replace('T', ' ')) %></dd><dt>Resolved</dt><dd><%= ticket.getResolvedAt() == null ? "Not resolved" : HtmlUtil.escape(ticket.getResolvedAt().toString().replace('T', ' ')) %></dd></dl></article>
            </section>
            <% if (ticket.getResolution() != null && !ticket.getResolution().isBlank()) { %>
                <section class="panel detail-card"><p class="eyebrow">Resolution</p><p class="detail-copy"><%= HtmlUtil.escape(ticket.getResolution()) %></p></section>
            <% } %>
            <section class="detail-grid comments-history-grid">
                <article class="panel detail-card">
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
                    <form method="post" action="${pageContext.request.contextPath}/<%= area %>/add-comment" class="comment-form" data-comment-form novalidate>
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                        <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
                        <label for="comment">Add a comment</label>
                        <textarea id="comment" name="comment" rows="4" maxlength="2000" required placeholder="Write an update or question..." data-comment-input></textarea>
                        <button class="button button-primary" type="submit">Add comment</button>
                    </form>
                </article>
                <article class="panel detail-card">
                    <p class="eyebrow">Status history</p>
                    <% if (history == null || history.isEmpty()) { %>
                        <p class="muted">No status history recorded.</p>
                    <% } else { %>
                        <ol class="history-list">
                        <% for (TicketHistory entry : history) { %>
                            <li><strong><%= HtmlUtil.escape(entry.getActionDescription()) %></strong><span><%= entry.getOldStatus() == null ? "Created" : HtmlUtil.escape(entry.getOldStatus().name().replace('_', ' ')) %> &rarr; <%= HtmlUtil.escape(entry.getNewStatus().name().replace('_', ' ')) %></span><small><%= entry.getChangedAt() == null ? "-" : HtmlUtil.escape(entry.getChangedAt().toString().replace('T', ' ')) %></small></li>
                        <% } %>
                        </ol>
                    <% } %>
                </article>
            </section>
        <% } %>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
