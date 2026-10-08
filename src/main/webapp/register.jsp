<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register | Smart Campus Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body class="auth-page">
<main class="auth-shell"><section class="auth-card auth-card-wide">
    <div class="brand brand-dark"><span class="brand-mark">SC</span><span>Smart Campus<br><strong>Helpdesk</strong></span></div>
    <p class="eyebrow">Get started</p>
    <h1>Create your student account</h1>
    <p class="auth-intro">Use your account to submit and follow campus support requests.</p>

    <% if (request.getAttribute("error") != null) { %>
        <p role="alert"><%= com.smartcampus.helpdesk.util.HtmlUtil.escape(String.valueOf(request.getAttribute("error"))) %></p>
    <% } %>

    <div class="client-error alert" data-form-error role="alert" hidden></div>
    <form method="post" action="${pageContext.request.contextPath}/register" data-auth-form="register" novalidate>
        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("security.csrfToken") %>">
        <p>
            <label for="name">Name</label>
            <input id="name" name="name" type="text" required maxlength="150" autocomplete="name">
        </p>
        <p>
            <label for="email">Email</label>
            <input id="email" name="email" type="email" required maxlength="254" autocomplete="email">
        </p>
        <p>
            <label for="department">Department</label>
            <input id="department" name="department" type="text" maxlength="150" autocomplete="organization">
        </p>
        <p>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" required autocomplete="new-password">
        </p>
        <p>
            <label for="confirmPassword">Confirm password</label>
            <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password">
        </p>
        <button type="submit">Register</button>
    </form>

    <p class="auth-footer">Already registered? <a href="${pageContext.request.contextPath}/login.jsp">Log in</a></p>
</section></main>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
