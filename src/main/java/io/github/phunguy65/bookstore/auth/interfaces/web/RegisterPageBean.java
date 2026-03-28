package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.auth.application.service.AuthApplicationService;
import io.github.phunguy65.bookstore.auth.application.service.RegisterResult;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class RegisterPageBean {
    private final AuthApplicationService authApplicationService;

    @Inject
    public RegisterPageBean(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    public RegisterPageModel handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (AuthSession.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + AuthPaths.ACCOUNT_HOME);
            return new RegisterPageModel("", "", "", null);
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new RegisterPageModel("", "", "", null);
        }

        String email = trimToEmpty(request.getParameter("email"));
        String fullName = trimToEmpty(request.getParameter("fullName"));
        String phoneNumber = trimToEmpty(request.getParameter("phoneNumber"));
        String password = trimToEmpty(request.getParameter("password"));
        String confirmPassword = trimToEmpty(request.getParameter("confirmPassword"));

        RegisterResult result = authApplicationService.register(
                email,
                password,
                confirmPassword,
                fullName,
                phoneNumber
        );
        if (result.isSuccess()) {
            response.sendRedirect(request.getContextPath() + AuthPaths.LOGIN + "?" + AuthPaths.REGISTERED_QUERY);
            return new RegisterPageModel(email, fullName, phoneNumber, null);
        }

        return new RegisterPageModel(email, fullName, phoneNumber, result.getErrorMessage());
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
