<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body class="landing-page">
<main class="landing-shell"><section class="landing-card">
    <div class="brand brand-dark"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
    <p class="eyebrow">Campus support, made simple</p>
    <h1>Resolve campus issues with clarity.</h1>
    <p class="landing-copy">Submit requests, follow progress, and keep every support conversation in one place.</p>
    <% if (session.getAttribute("auth.userId") == null) { %>
        <div class="landing-actions"><a class="button button-primary" href="${pageContext.request.contextPath}/login.jsp">Log in</a><a class="button button-secondary" href="${pageContext.request.contextPath}/register.jsp">Register as a student</a></div>
    <% } else { %>
        <% String userRole = String.valueOf(session.getAttribute("auth.userRole"));
           String dashboardArea = "ADMIN".equals(userRole) ? "admin" : ("STAFF".equals(userRole) ? "staff" : "student"); %>
        <div class="signed-in-card"><span class="muted-label">Current workspace</span><strong><%= dashboardArea.substring(0, 1).toUpperCase() + dashboardArea.substring(1) %> dashboard</strong><p>You are signed in and ready to continue.</p><div class="landing-actions"><a class="button button-primary" href="${pageContext.request.contextPath}/<%= dashboardArea %>/dashboard">Open dashboard</a><form method="post" action="${pageContext.request.contextPath}/logout" data-confirm="Are you sure you want to log out?"><input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>"><button class="button button-secondary" type="submit">Log out</button></form></div></div>
    <% } %>
</section></main>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
