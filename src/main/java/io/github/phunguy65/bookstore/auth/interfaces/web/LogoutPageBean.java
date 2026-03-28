package io.github.phunguy65.bookstore.auth.interfaces.web;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class LogoutPageBean {
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthSession.signOut(request);
        response.sendRedirect(request.getContextPath() + AuthPaths.LOGIN);
    }
}
