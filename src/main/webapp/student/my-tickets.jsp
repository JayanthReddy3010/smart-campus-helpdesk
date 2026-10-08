<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.smartcampus.helpdesk.model.Category" %>
<%@ page import="com.smartcampus.helpdesk.model.Ticket" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%
    String role = String.valueOf(session.getAttribute("auth.userRole"));
    String area = "STAFF".equals(role) || "ADMIN".equals(role) ? "staff" : "student";
    String name = HtmlUtil.escape((String) session.getAttribute("auth.userName"));
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String selectedStatus = String.valueOf(request.getAttribute("selectedStatus"));
    String selectedPriority = String.valueOf(request.getAttribute("selectedPriority"));
    String selectedCategory = String.valueOf(request.getAttribute("selectedCategory"));
    String search = HtmlUtil.escape(String.valueOf(request.getAttribute("search")));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Tickets | Smart Campus Helpdesk</title>
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
            <div><p class="eyebrow">Workspace</p><h1><%= name %>'s tickets</h1></div>
            <span class="profile-chip">Only your tickets</span>
        </header>

        <% if (request.getAttribute("error") != null) { %><div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div><% } %>

        <section class="section-heading"><div><p class="eyebrow">Requests</p><h2>Ticket history</h2></div></section>
        <form class="filter-bar panel" method="get" action="${pageContext.request.contextPath}/<%= area %>/tickets" data-filter-form>
            <label>Status<select name="status"><option value="">All statuses</option><option value="OPEN" <%= "OPEN".equals(selectedStatus) ? "selected" : "" %>>Open</option><option value="ASSIGNED" <%= "ASSIGNED".equals(selectedStatus) ? "selected" : "" %>>Assigned</option><option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(selectedStatus) ? "selected" : "" %>>In progress</option><option value="RESOLVED" <%= "RESOLVED".equals(selectedStatus) ? "selected" : "" %>>Resolved</option><option value="CLOSED" <%= "CLOSED".equals(selectedStatus) ? "selected" : "" %>>Closed</option></select></label>
            <label>Priority<select name="priority"><option value="">All priorities</option><option value="LOW" <%= "LOW".equals(selectedPriority) ? "selected" : "" %>>Low</option><option value="MEDIUM" <%= "MEDIUM".equals(selectedPriority) ? "selected" : "" %>>Medium</option><option value="HIGH" <%= "HIGH".equals(selectedPriority) ? "selected" : "" %>>High</option><option value="URGENT" <%= "URGENT".equals(selectedPriority) ? "selected" : "" %>>Urgent</option></select></label>
            <label>Category<select name="category"><option value="">All categories</option><% if (categories != null) { for (Category category : categories) { %><option value="<%= category.getCategoryId() %>" <%= String.valueOf(category.getCategoryId()).equals(selectedCategory) ? "selected" : "" %>><%= HtmlUtil.escape(category.getCategoryName()) %></option><% } } %></select></label>
            <label class="filter-search">Search<input type="search" name="search" value="<%= search %>" maxlength="100" placeholder="Ticket ID or subject" data-filter-search></label>
            <button class="button button-primary filter-button" type="submit">Apply filters</button>
            <a class="button button-secondary filter-button" href="${pageContext.request.contextPath}/<%= area %>/tickets">Clear</a>
        </form>
        <section class="panel table-panel">
            <% if (tickets == null || tickets.isEmpty()) { %>
                <div class="empty-state"><h3>No tickets found</h3><p>You have not submitted any helpdesk tickets.</p></div>
            <% } else { %>
                <div class="table-wrap"><table><thead><tr><th>Ticket</th><th>Subject</th><th>Priority</th><th>Status</th><th>Updated</th></tr></thead><tbody>
                <% for (Ticket ticket : tickets) { %>
                    <tr>
                        <td><a class="ticket-id" href="${pageContext.request.contextPath}/<%= area %>/ticket?id=<%= ticket.getTicketId() %>">#<%= ticket.getTicketId() %></a></td>
                        <td><strong><%= HtmlUtil.escape(ticket.getSubject()) %></strong><small><%= HtmlUtil.escape(ticket.getLocation()) %></small></td>
                        <td><span class="priority priority-<%= ticket.getPriority().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getPriority().name()) %></span></td>
                        <td><span class="status status-<%= ticket.getStatus().name().toLowerCase() %>"><%= HtmlUtil.escape(ticket.getStatus().name().replace('_', ' ')) %></span></td>
                        <td><%= ticket.getUpdatedAt() == null ? "-" : HtmlUtil.escape(ticket.getUpdatedAt().toString().replace('T', ' ')) %></td>
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
