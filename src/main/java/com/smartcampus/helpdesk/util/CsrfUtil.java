package com.smartcampus.helpdesk.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfUtil {

    public static final String SESSION_KEY = "security.csrfToken";
    public static final String REQUEST_PARAMETER = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    public static String ensureToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object existing = session.getAttribute(SESSION_KEY);
        if (existing instanceof String token && !token.isBlank()) {
            return token;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_KEY, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_KEY);
        String submitted = request.getParameter(REQUEST_PARAMETER);
        return expected instanceof String && submitted != null
                && java.security.MessageDigest.isEqual(
                ((String) expected).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
