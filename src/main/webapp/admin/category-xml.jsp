<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.smartcampus.helpdesk.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Category XML Exchange | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="brand"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
        <nav class="nav-menu" aria-label="Admin navigation">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Admin overview</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/tickets">All tickets</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/category-xml">Category XML</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="nav-link nav-button" type="submit">Log out</button></form>
        </nav>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" type="button" data-nav-toggle aria-label="Toggle navigation">Menu</button>
            <div><p class="eyebrow">Data exchange</p><h1>Category XML</h1></div>
            <span class="profile-chip">Admin only</span>
        </header>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert" role="alert"><%= HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></div>
        <% } %>
        <% if (request.getParameter("imported") != null) { %>
            <div class="success" role="status">Imported <%= HtmlUtil.escape(request.getParameter("imported")) %> category record(s).</div>
        <% } %>

        <section class="xml-grid">
            <article class="panel detail-card">
                <p class="eyebrow">Export</p>
                <h2>Download category configuration</h2>
                <p class="muted">Exports category names, descriptions, and active flags as an XML document.</p>
                <a class="button button-primary" href="${pageContext.request.contextPath}/admin/categories/export.xml">Download XML</a>
            </article>
            <article class="panel detail-card">
                <p class="eyebrow">Import</p>
                <h2>Upload category configuration</h2>
                <p class="muted">The file is checked against the bundled XSD before categories are updated.</p>
                <form method="post" action="${pageContext.request.contextPath}/admin/categories/import" enctype="multipart/form-data" data-confirm="Import this category configuration?">
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
                    <label for="categoriesFile">Category XML file</label>
                    <input id="categoriesFile" name="categoriesFile" type="file" accept="application/xml,.xml" required>
                    <button class="button button-primary" type="submit">Import XML</button>
                </form>
            </article>
        </section>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
