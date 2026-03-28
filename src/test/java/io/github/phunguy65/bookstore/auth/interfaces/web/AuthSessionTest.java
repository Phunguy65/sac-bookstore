package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthSessionTest {
    @Test
    void returnsFalseWhenNoSessionExists() {
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");

        assertFalse(AuthSession.isAuthenticated(requestContext.proxy()));
        assertNull(AuthSession.getCustomerId(requestContext.proxy()));
    }

    @Test
    void signsInByCreatingSessionAttribute() {
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");

        AuthSession.signIn(requestContext.proxy(), new CustomerId(7L));

        assertTrue(AuthSession.isAuthenticated(requestContext.proxy()));
        assertEquals(7L, AuthSession.getCustomerId(requestContext.proxy()).value());
        assertEquals(7L, requestContext.session().attribute(AuthSession.AUTH_CUSTOMER_ID));
    }

    @Test
    void signsOutWithoutInvalidatingSession() {
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(9L);
        requestContext.session().proxy().setAttribute("cartDraft", "kept");

        AuthSession.signOut(requestContext.proxy());

        assertFalse(AuthSession.isAuthenticated(requestContext.proxy()));
        assertNull(requestContext.session().attribute(AuthSession.AUTH_CUSTOMER_ID));
        assertEquals("kept", requestContext.session().attribute("cartDraft"));
    }
}
