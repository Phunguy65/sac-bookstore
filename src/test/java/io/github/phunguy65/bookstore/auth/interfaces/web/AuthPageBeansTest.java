package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.auth.application.service.AuthApplicationService;
import io.github.phunguy65.bookstore.auth.application.service.DemoModeSettings;
import io.github.phunguy65.bookstore.auth.application.service.LoginResult;
import io.github.phunguy65.bookstore.auth.application.service.RegisterResult;
import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthPageBeansTest {
    @Test
    void jspResolvedAuthBeansUseDependentScope() {
        assertDependentScope(LoginPageBean.class);
        assertDependentScope(RegisterPageBean.class);
        assertDependentScope(LogoutPageBean.class);
        assertDependentScope(AuthGuardBean.class);
    }

    @Test
    void constructorInjectedAuthBeansDeclareRequiredDependencies() {
        assertConstructorDependencies(LoginPageBean.class, AuthApplicationService.class, DemoModeSettings.class);
        assertSingleConstructorDependency(RegisterPageBean.class, AuthApplicationService.class);
    }

    @Test
    void loginPageRedirectsAuthenticatedUser() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(false));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(11L);
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/account/index.jsp", responseContext.redirectedUrl());
    }

    @Test
    void loginPageDisplaysEmptyFormOnGet() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(true));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals(null, model.getErrorMessage());
        assertEquals(null, model.getInfoMessage());
        assertTrue(model.isShowDevCredentials());
        assertEquals("dev@bookstore.local", model.getDevEmail());
        assertEquals("dev123456", model.getDevPassword());
    }

    @Test
    void loginPageShowsRegisteredMessageOnGet() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(true));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .parameter("registered", "1");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("Dang ky thanh cong. Vui long dang nhap.", model.getInfoMessage());
        assertEquals(null, model.getErrorMessage());
        assertTrue(model.isShowDevCredentials());
        assertEquals("dev@bookstore.local", model.getDevEmail());
        assertEquals("dev123456", model.getDevPassword());
    }

    @Test
    void loginPageTreatsDeleteAsFormDisplay() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(false));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("DELETE", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("password", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals(null, model.getErrorMessage());
        assertFalse(model.isShowDevCredentials());
        assertEquals(null, model.getDevEmail());
        assertEquals(null, model.getDevPassword());
    }

    @Test
    void loginPageStoresSessionAndRedirectsOnSuccess() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.loginResult = LoginResult.success(customer());
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(false));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("password", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/account/index.jsp", responseContext.redirectedUrl());
        assertEquals(1L, requestContext.session().attribute(AuthSession.AUTH_CUSTOMER_ID));
    }

    @Test
    void loginPageReturnsInlineErrorOnFailure() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.loginResult = LoginResult.failure("Bad credentials");
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(true));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("password", "wrong");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("Bad credentials", model.getErrorMessage());
        assertEquals(null, model.getInfoMessage());
        assertTrue(model.isShowDevCredentials());
        assertEquals("dev@bookstore.local", model.getDevEmail());
        assertEquals("dev123456", model.getDevPassword());
    }

    @Test
    void loginPageConvertsMissingEmailToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.loginResult = LoginResult.failure("email must not be blank");
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(true));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("password", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals("email must not be blank", model.getErrorMessage());
        assertTrue(model.isShowDevCredentials());
        assertEquals("dev@bookstore.local", model.getDevEmail());
        assertEquals("dev123456", model.getDevPassword());
    }

    @Test
    void loginPageConvertsMissingPasswordToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.loginResult = LoginResult.failure("rawPassword must not be blank");
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(true));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("rawPassword must not be blank", model.getErrorMessage());
        assertEquals(null, model.getInfoMessage());
        assertTrue(model.isShowDevCredentials());
        assertEquals("dev@bookstore.local", model.getDevEmail());
        assertEquals("dev123456", model.getDevPassword());
    }

    @Test
    void loginPageHidesDevCredentialsWhenNotInDemoMode() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(false));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertFalse(model.isShowDevCredentials());
        assertEquals(null, model.getDevEmail());
        assertEquals(null, model.getDevPassword());
    }

    @Test
    void registerPageRedirectsAuthenticatedUser() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(12L);
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/account/index.jsp", responseContext.redirectedUrl());
    }

    @Test
    void registerPageTreatsDeleteAsFormDisplay() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("DELETE", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals("", model.getFullName());
        assertEquals("", model.getPhoneNumber());
        assertEquals(null, model.getErrorMessage());
    }

    @Test
    void registerPageDisplaysEmptyFormOnGet() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals("", model.getFullName());
        assertEquals("", model.getPhoneNumber());
        assertEquals(null, model.getErrorMessage());
    }

    @Test
    void registerPageConvertsMissingEmailToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("email must not be blank");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals("Nguyen Van Doc", model.getFullName());
        assertEquals("0901234567", model.getPhoneNumber());
        assertEquals("email must not be blank", model.getErrorMessage());
    }

    @Test
    void registerPageConvertsMissingFullNameToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("fullName must not be blank");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("", model.getFullName());
        assertEquals("0901234567", model.getPhoneNumber());
        assertEquals("fullName must not be blank", model.getErrorMessage());
    }

    @Test
    void registerPageRedirectsToLoginWithSuccessFlag() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.success(customer());
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/auth/login.jsp?registered=1", responseContext.redirectedUrl());
    }

    @Test
    void registerPageReturnsInlineErrorAndKeepsFields() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("Email da duoc su dung.");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("Nguyen Van Doc", model.getFullName());
        assertEquals("0901234567", model.getPhoneNumber());
        assertEquals("Email da duoc su dung.", model.getErrorMessage());
    }

    @Test
    void registerPageConvertsMissingPasswordToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("rawPassword must not be blank");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("Nguyen Van Doc", model.getFullName());
        assertEquals("0901234567", model.getPhoneNumber());
        assertEquals("rawPassword must not be blank", model.getErrorMessage());
    }

    @Test
    void registerPageConvertsMissingConfirmPasswordToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("Xac nhan mat khau khong khop.");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567")
                .parameter("password", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("Nguyen Van Doc", model.getFullName());
        assertEquals("0901234567", model.getPhoneNumber());
        assertEquals("Xac nhan mat khau khong khop.", model.getErrorMessage());
    }

    @Test
    void registerPageConvertsMissingPhoneNumberToEmptyString() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.registerResult = RegisterResult.failure("phoneNumber must be a valid phone number");
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("password", "secret-123")
                .parameter("confirmPassword", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
        assertEquals("Nguyen Van Doc", model.getFullName());
        assertEquals("", model.getPhoneNumber());
        assertEquals("phoneNumber must be a valid phone number", model.getErrorMessage());
    }

    @Test
    void loginPageTrimsEmailWhitespace() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        service.loginResult = LoginResult.failure("Bad credentials");
        LoginPageBean bean = new LoginPageBean(service, new StubDemoModeConfig(false));
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .parameter("email", "  reader@example.com  ")
                .parameter("password", "secret-123");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        LoginPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("reader@example.com", model.getEmail());
    }

    @Test
    void registerPageIgnoresParametersOnGet() throws IOException {
        StubAuthApplicationService service = new StubAuthApplicationService();
        RegisterPageBean bean = new RegisterPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .parameter("email", "reader@example.com")
                .parameter("fullName", "Nguyen Van Doc")
                .parameter("phoneNumber", "0901234567");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        RegisterPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
        assertEquals("", model.getEmail());
        assertEquals("", model.getFullName());
        assertEquals("", model.getPhoneNumber());
    }

    @Test
    void logoutPageRemovesOnlyAuthAttributeAndRedirects() throws IOException {
        LogoutPageBean bean = new LogoutPageBean();
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .authenticated(5L);
        requestContext.session().proxy().setAttribute("cartDraft", "keep-me");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/auth/login.jsp", responseContext.redirectedUrl());
        assertEquals("keep-me", requestContext.session().attribute("cartDraft"));
        assertEquals(null, requestContext.session().attribute(AuthSession.AUTH_CUSTOMER_ID));
    }

    @Test
    void authGuardRedirectsGuestsToLogin() throws IOException {
        AuthGuardBean bean = new AuthGuardBean();
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.requireAuthenticated(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/auth/login.jsp", responseContext.redirectedUrl());
    }

    @Test
    void authGuardLetsAuthenticatedUsersContinue() throws IOException {
        AuthGuardBean bean = new AuthGuardBean();
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(3L);
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.requireAuthenticated(requestContext.proxy(), responseContext.proxy());

        assertFalse(responseContext.committed());
    }

    private void assertDependentScope(Class<?> beanClass) {
        assertTrue(beanClass.isAnnotationPresent(jakarta.enterprise.context.Dependent.class));
        assertFalse(beanClass.isAnnotationPresent(jakarta.enterprise.context.RequestScoped.class));
    }

    private void assertSingleConstructorDependency(Class<?> beanClass, Class<?> dependencyType) {
        assertEquals(1, beanClass.getDeclaredConstructors().length);
        assertEquals(1, beanClass.getDeclaredConstructors()[0].getParameterCount());
        assertEquals(dependencyType, beanClass.getDeclaredConstructors()[0].getParameterTypes()[0]);
    }

    private void assertConstructorDependencies(Class<?> beanClass, Class<?>... dependencyTypes) {
        assertEquals(1, beanClass.getDeclaredConstructors().length);
        assertEquals(dependencyTypes.length, beanClass.getDeclaredConstructors()[0].getParameterCount());
        assertEquals(java.util.List.of(dependencyTypes), java.util.List.of(beanClass.getDeclaredConstructors()[0].getParameterTypes()));
    }

    private static final class StubDemoModeConfig implements DemoModeSettings {
        private final boolean enabled;

        private StubDemoModeConfig(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getEmail() {
            return enabled ? "dev@bookstore.local" : null;
        }

        @Override
        public String getPassword() {
            return enabled ? "dev123456" : null;
        }
    }

    private static Customer customer() {
        Instant now = Instant.parse("2026-03-27T00:00:00Z");
        return new Customer(
                new CustomerId(1L),
                new Email("reader@example.com"),
                new PasswordHash("HASH::secret-123"),
                new FullName("Nguyen Van Doc"),
                new PhoneNumber("0901234567"),
                CustomerStatus.ACTIVE,
                0L,
                now,
                now
        );
    }

    private static final class StubAuthApplicationService extends AuthApplicationService {
        private LoginResult loginResult = LoginResult.failure("missing");
        private RegisterResult registerResult = RegisterResult.failure("missing");

        @Override
        public LoginResult login(String email, String rawPassword) {
            return loginResult;
        }

        @Override
        public RegisterResult register(String email, String rawPassword, String confirmPassword, String fullName, String phoneNumber) {
            return registerResult;
        }
    }
}
