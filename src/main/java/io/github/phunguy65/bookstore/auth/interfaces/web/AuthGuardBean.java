package io.github.phunguy65.bookstore.auth.interfaces.web;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class AuthGuardBean {
    public void requireAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!AuthSession.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + AuthPaths.LOGIN);
        }
    }
}
