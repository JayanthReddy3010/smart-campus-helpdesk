<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%
    String name = HtmlUtil.escape((String) session.getAttribute("auth.userName"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="brand"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
        <nav class="nav-menu" aria-label="Admin navigation">
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">Admin overview</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/tickets">All tickets</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/category-xml">Category XML</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="nav-link nav-button" type="submit">Log out</button></form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div><p class="eyebrow">Administration</p><h1>Helpdesk overview</h1></div>
            <div class="profile-chip"><span class="avatar">A</span><span>Administrator</span></div>
        </header>
        <p class="muted admin-welcome">Signed in as <%= name %>. Monitor service requests and operational workload from here.</p>

        <% if (request.getAttribute("error") != null) { %><div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div><% } %>

        <section class="stats-grid admin-stats" aria-label="Ticket administration summary">
            <article class="stat-card"><span class="stat-label">Total tickets</span><strong>${empty totalTickets ? 0 : totalTickets}</strong><span class="stat-accent accent-blue"></span></article>
            <article class="stat-card"><span class="stat-label">Open</span><strong>${empty openTickets ? 0 : openTickets}</strong><span class="stat-accent accent-amber"></span></article>
            <article class="stat-card"><span class="stat-label">Assigned</span><strong>${empty assignedTickets ? 0 : assignedTickets}</strong><span class="stat-accent accent-violet"></span></article>
            <article class="stat-card"><span class="stat-label">In progress</span><strong>${empty inProgressTickets ? 0 : inProgressTickets}</strong><span class="stat-accent accent-violet"></span></article>
            <article class="stat-card"><span class="stat-label">Resolved</span><strong>${empty resolvedTickets ? 0 : resolvedTickets}</strong><span class="stat-accent accent-green"></span></article>
            <article class="stat-card"><span class="stat-label">Closed</span><strong>${empty closedTickets ? 0 : closedTickets}</strong><span class="stat-accent accent-green"></span></article>
            <article class="stat-card stat-card-alert"><span class="stat-label">High / urgent</span><strong>${empty highPriorityTickets ? 0 : highPriorityTickets}</strong><span class="stat-accent accent-red"></span></article>
        </section>

        <section class="admin-callout panel">
            <div><p class="eyebrow">Queue management</p><h2>Review the complete ticket queue</h2><p class="muted">Filter requests by status, priority, category, or search by ticket number and subject.</p></div>
            <a class="button button-primary" href="${pageContext.request.contextPath}/admin/tickets">Open ticket list</a>
        </section>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
