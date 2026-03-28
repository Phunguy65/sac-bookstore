package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AuthSession {
    public static final String AUTH_CUSTOMER_ID = "authCustomerId";

    private AuthSession() {
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return getCustomerId(request) != null;
    }

    public static CustomerId getCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(AUTH_CUSTOMER_ID);
        if (value instanceof Long customerId) {
            return new CustomerId(customerId);
        }
        return null;
    }

    public static void signIn(HttpServletRequest request, CustomerId customerId) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AUTH_CUSTOMER_ID, customerId.value());
    }

    public static void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(AUTH_CUSTOMER_ID);
        }
    }
}
