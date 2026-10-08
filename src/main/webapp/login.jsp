<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body class="auth-page">
<main class="auth-shell"><section class="auth-card">
    <div class="brand brand-dark"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
    <p class="eyebrow">Welcome back</p>
    <h1>Log in to your workspace</h1>
    <p class="auth-intro">Track requests and stay connected with campus support.</p>

    <% if (request.getAttribute("error") != null) { %>
        <p role="alert"><%= com.smartcampus.helpdesk.util.HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></p>
    <% } else if ("true".equals(request.getParameter("registered"))) { %>
        <p role="status">Registration successful. You can now log in.</p>
    <% } else if ("true".equals(request.getParameter("loggedOut"))) { %>
        <p role="status">You have been logged out.</p>
    <% } else if ("auth".equals(request.getParameter("error"))) { %>
        <p role="alert">Please log in to continue.</p>
    <% } %>

    <div class="client-error alert" data-form-error role="alert" hidden></div>
    <form method="post" action="${pageContext.request.contextPath}/login" data-auth-form="login" novalidate>
        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
        <p>
            <label for="email">Email</label>
            <input id="email" name="email" type="text" required maxlength="254" autocomplete="username">
        </p>
        <p>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" required autocomplete="current-password">
        </p>
        <button type="submit">Log in</button>
    </form>

    <p class="auth-footer">Need an account? <a href="${pageContext.request.contextPath}/register.jsp">Register as a student</a></p>
</section></main>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
