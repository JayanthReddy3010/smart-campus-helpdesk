<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.smartcampus.helpdesk.model.Ticket" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%
    String role = String.valueOf(session.getAttribute("auth.userRole"));
    String area = "STAFF".equals(role) || "ADMIN".equals(role) ? "staff" : "student";
    String name = HtmlUtil.escape((String) session.getAttribute("auth.userName"));
    String email = HtmlUtil.escape((String) session.getAttribute("auth.userEmail"));
    List<Ticket> recentTickets = (List<Ticket>) request.getAttribute("recentTickets");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="brand"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
        <nav class="nav-menu" aria-label="Main navigation">
            <a class="nav-link active" href="${pageContext.request.contextPath}/<%= area %>/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/create-ticket">Create ticket</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/tickets">My tickets</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?">
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                <button class="nav-link nav-button" type="submit">Log out</button>
            </form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div>
                <p class="eyebrow">Overview</p>
                <h1>Welcome back, <%= name %></h1>
            </div>
            <div class="profile-chip"><span class="avatar"><%= name.isEmpty() ? "U" : name.substring(0, 1).toUpperCase() %></span><span><%= HtmlUtil.escape(role) %></span></div>
        </header>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div>
        <% } %>

        <section class="profile-strip" aria-label="Logged-in user information">
            <div><span class="muted-label">Account</span><strong><%= email %></strong></div>
            <div><span class="muted-label">Role</span><strong><%= HtmlUtil.escape(role) %></strong></div>
            <div><span class="muted-label">Workspace</span><strong>My tickets</strong></div>
        </section>

        <section class="stats-grid" aria-label="Ticket summary">
            <article class="stat-card"><span class="stat-label">Total tickets</span><strong>${empty totalTickets ? 0 : totalTickets}</strong><span class="stat-accent accent-blue"></span></article>
            <article class="stat-card"><span class="stat-label">Open</span><strong>${empty openTickets ? 0 : openTickets}</strong><span class="stat-accent accent-amber"></span></article>
            <article class="stat-card"><span class="stat-label">In progress</span><strong>${empty inProgressTickets ? 0 : inProgressTickets}</strong><span class="stat-accent accent-violet"></span></article>
            <article class="stat-card"><span class="stat-label">Resolved</span><strong>${empty resolvedTickets ? 0 : resolvedTickets}</strong><span class="stat-accent accent-green"></span></article>
        </section>

        <section class="section-heading">
            <div><p class="eyebrow">Activity</p><h2>Recent tickets</h2></div>
            <a class="text-link" href="${pageContext.request.contextPath}/<%= area %>/tickets">View all tickets</a>
        </section>
        <section class="panel table-panel">
            <% if (recentTickets == null || recentTickets.isEmpty()) { %>
                <div class="empty-state"><h3>No tickets yet</h3><p>Your submitted tickets will appear here.</p></div>
            <% } else { %>
                <div class="table-wrap"><table><thead><tr><th>Ticket</th><th>Subject</th><th>Status</th><th>Created</th></tr></thead><tbody>
                <% for (Ticket ticket : recentTickets) { %>
                    <tr>
                        <td><a class="ticket-id" href="${pageContext.request.contextPath}/<%= area %>/ticket?id=<%= ticket.getTicketId() %>">#<%= ticket.getTicketId() %></a></td>
                        <td><%= HtmlUtil.escape(ticket.getSubject()) %></td>
                        <td><span class="status status-<%= ticket.getStatus().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getStatus().name().replace('_', ' ')) %></span></td>
                        <td><%= ticket.getCreatedAt() == null ? "-" : HtmlUtil.escape(ticket.getCreatedAt().toString().replace('T', ' ')) %></td>
                    </tr>
                <% } %>
                </tbody></table></div>
            <% } %>
        </section>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
