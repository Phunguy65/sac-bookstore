package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.auth.application.service.AuthApplicationService;
import io.github.phunguy65.bookstore.auth.application.service.LoginResult;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class LoginPageBean {
    private final AuthApplicationService authApplicationService;

    @Inject
    public LoginPageBean(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    public LoginPageModel handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (AuthSession.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + AuthPaths.ACCOUNT_HOME);
            return new LoginPageModel("", null, null);
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            String infoMessage = null;
            if ("1".equals(request.getParameter("registered"))) {
                infoMessage = "Dang ky thanh cong. Vui long dang nhap.";
            }
            return new LoginPageModel("", null, infoMessage);
        }

        String email = trimToEmpty(request.getParameter("email"));
        String password = trimToEmpty(request.getParameter("password"));
        LoginResult result = authApplicationService.login(email, password);
        if (result.isSuccess()) {
            AuthSession.signIn(request, result.getCustomer().id());
            response.sendRedirect(request.getContextPath() + AuthPaths.ACCOUNT_HOME);
            return new LoginPageModel(email, null, null);
        }

        return new LoginPageModel(email, result.getErrorMessage(), null);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
