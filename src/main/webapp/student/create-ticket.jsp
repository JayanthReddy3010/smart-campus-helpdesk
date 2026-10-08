<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.smartcampus.helpdesk.model.Category" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<%
    String role = String.valueOf(session.getAttribute("auth.userRole"));
    String area = "STAFF".equals(role) || "ADMIN".equals(role) ? "staff" : "student";
    String name = HtmlUtil.escape((String) session.getAttribute("auth.userName"));
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String formCategoryId = String.valueOf(request.getAttribute("formCategoryId"));
    String formSubject = String.valueOf(request.getAttribute("formSubject"));
    String formDescription = String.valueOf(request.getAttribute("formDescription"));
    String formLocation = String.valueOf(request.getAttribute("formLocation"));
    String formPriority = String.valueOf(request.getAttribute("formPriority"));
    String formToken = HtmlUtil.escape(String.valueOf(session.getAttribute("createTicket.formToken")));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create Ticket | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="brand"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
        <nav class="nav-menu" aria-label="Main navigation">
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/dashboard">Dashboard</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/<%= area %>/create-ticket">Create ticket</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/<%= area %>/tickets">My tickets</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="nav-link nav-button" type="submit">Log out</button></form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div><p class="eyebrow">New request</p><h1>Create a helpdesk ticket</h1></div>
            <span class="profile-chip">Signed in as <%= name %></span>
        </header>

        <section class="form-layout">
            <div class="form-intro">
                <p class="eyebrow">Tell us what happened</p>
                <h2>We will route your request to the right campus team.</h2>
                <p class="muted">Provide clear details so the support team can respond quickly. Fields marked with * are required.</p>
            </div>
            <section class="panel form-panel">
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div>
                <% } %>
                <div class="client-error alert" data-form-error role="alert" hidden></div>
                    <form method="post" action="${pageContext.request.contextPath}/<%= area %>/create-ticket" data-ticket-form data-confirm="Submit this ticket?" novalidate>
                    <input type="hidden" name="formToken" value="<%= formToken %>">
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                    <div class="form-grid">
                        <label>Category *<select name="categoryId" required data-required-field><option value="">Select a category</option><% if (categories != null) { for (Category category : categories) { %><option value="<%= category.getCategoryId() %>" <%= String.valueOf(category.getCategoryId()).equals(formCategoryId) ? "selected" : "" %>><%= HtmlUtil.escape(category.getCategoryName()) %></option><% } } %></select></label>
                        <label>Priority *<select name="priority" required data-required-field><option value="">Select priority</option><option value="LOW" <%= "LOW".equals(formPriority) ? "selected" : "" %>>Low</option><option value="MEDIUM" <%= "MEDIUM".equals(formPriority) || "null".equals(formPriority) ? "selected" : "" %>>Medium</option><option value="HIGH" <%= "HIGH".equals(formPriority) ? "selected" : "" %>>High</option><option value="URGENT" <%= "URGENT".equals(formPriority) ? "selected" : "" %>>Urgent</option></select></label>
                    </div>
                    <label>Subject *<input type="text" name="subject" value="<%= formSubject %>" maxlength="200" required data-required-field data-label="Subject"><span class="field-hint">Up to 200 characters.</span></label>
                    <label>Description *<textarea name="description" rows="7" maxlength="5000" required data-required-field data-label="Description"><%= formDescription %></textarea><span class="field-hint">Include useful context, steps already tried, and any impact.</span></label>
                    <label>Location *<input type="text" name="location" value="<%= formLocation %>" maxlength="255" required data-required-field data-label="Location" placeholder="For example: Block B, Room 204"><span class="field-hint">Where should the team investigate?</span></label>
                    <div class="form-actions"><a class="button button-secondary" href="${pageContext.request.contextPath}/<%= area %>/dashboard">Cancel</a><button class="button button-primary" type="submit" data-submit-button>Submit ticket</button></div>
                </form>
            </section>
        </section>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
