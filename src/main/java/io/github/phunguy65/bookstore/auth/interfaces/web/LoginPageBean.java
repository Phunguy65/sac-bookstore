package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.auth.application.service.AuthApplicationService;
import io.github.phunguy65.bookstore.auth.application.service.DemoModeSettings;
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
    private final DemoModeSettings demoModeConfig;

    @Inject
    public LoginPageBean(AuthApplicationService authApplicationService, DemoModeSettings demoModeConfig) {
        this.authApplicationService = authApplicationService;
        this.demoModeConfig = demoModeConfig;
    }

    public LoginPageModel handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (AuthSession.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + AuthPaths.ACCOUNT_HOME);
            return pageModel("", null, null);
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            String infoMessage = null;
            if ("1".equals(request.getParameter("registered"))) {
                infoMessage = "Dang ky thanh cong. Vui long dang nhap.";
            }
            return pageModel("", null, infoMessage);
        }

        String email = trimToEmpty(request.getParameter("email"));
        String password = trimToEmpty(request.getParameter("password"));
        LoginResult result = authApplicationService.login(email, password);
        if (result.isSuccess()) {
            AuthSession.signIn(request, result.getCustomer().id());
            response.sendRedirect(request.getContextPath() + AuthPaths.ACCOUNT_HOME);
            return pageModel(email, null, null);
        }

        return pageModel(email, result.getErrorMessage(), null);
    }

    private LoginPageModel pageModel(String email, String errorMessage, String infoMessage) {
        return new LoginPageModel(
                email,
                errorMessage,
                infoMessage,
                demoModeConfig.isEnabled(),
                demoModeConfig.getEmail(),
                demoModeConfig.getPassword()
        );
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
