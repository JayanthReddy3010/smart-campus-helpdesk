package com.smartcampus.helpdesk.util;

import com.smartcampus.helpdesk.model.User;
import com.smartcampus.helpdesk.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AuthUtil {

    public static final String USER_ID = "auth.userId";
    public static final String USER_NAME = "auth.userName";
    public static final String USER_EMAIL = "auth.userEmail";
    public static final String USER_ROLE = "auth.userRole";

    private AuthUtil() {
    }

    public static void signIn(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(USER_ID, user.getUserId());
        session.setAttribute(USER_NAME, user.getName());
        session.setAttribute(USER_EMAIL, user.getEmail());
        session.setAttribute(USER_ROLE, user.getRole());
    }

    public static void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static boolean isSignedIn(HttpServletRequest request) {
        return request.getSession(false) != null
                && request.getSession(false).getAttribute(USER_ID) != null
                && getRole(request) != null;
    }

    public static UserRole getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object role = session.getAttribute(USER_ROLE);
        return role instanceof UserRole ? (UserRole) role : null;
    }

    public static Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userId = session == null ? null : session.getAttribute(USER_ID);
        return userId instanceof Integer ? (Integer) userId : null;
    }

    public static String getUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userName = session == null ? null : session.getAttribute(USER_NAME);
        return userName instanceof String ? (String) userName : null;
    }

    public static String getUserEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userEmail = session == null ? null : session.getAttribute(USER_EMAIL);
        return userEmail instanceof String ? (String) userEmail : null;
    }
}
