package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.util.AuthUtil;
import com.smartcampus.helpdesk.util.CsrfUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "same-origin");
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; script-src 'self'; style-src 'self'; "
                + "img-src 'self' data:; form-action 'self'; frame-ancestors 'none'; "
                + "base-uri 'self'; object-src 'none'");
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            if (!CsrfUtil.isValid(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request token.");
                return;
            }
        } else {
            CsrfUtil.ensureToken(httpRequest);
        }

        if (!isProtectedPath(path)) {
            if ("/login".equals(path) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
                demoLogin(httpRequest, httpResponse);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        if (!AuthUtil.isSignedIn(httpRequest)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        UserRole role = AuthUtil.getRole(httpRequest);
        if (!isAuthorized(path, role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this page.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void demoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String identity = request.getParameter("email");
        identity = identity == null ? "" : identity.trim();
        if (identity.isBlank()) {
            identity = "demo-user";
        }

        // Demo authentication: credentials are intentionally not verified for college project demonstration.
        User demoUser = new User(-1, identity, identity, null, UserRole.STUDENT, null, null);
        AuthUtil.signIn(request, demoUser);
        response.sendRedirect(request.getContextPath() + "/student/dashboard?demo=true");
    }

    private boolean isProtectedPath(String path) {
        return isSection(path, "/student")
                || isSection(path, "/staff")
                || isSection(path, "/admin");
    }

    private boolean isSection(String path, String section) {
        return path.equals(section) || path.startsWith(section + "/");
    }

    private boolean isAuthorized(String path, UserRole role) {
        if (isSection(path, "/admin")) {
            return role == UserRole.ADMIN;
        }
        if (isSection(path, "/staff")) {
            return role == UserRole.STAFF || role == UserRole.ADMIN;
        }
        return role == UserRole.STUDENT || role == UserRole.STAFF || role == UserRole.ADMIN;
    }
}
