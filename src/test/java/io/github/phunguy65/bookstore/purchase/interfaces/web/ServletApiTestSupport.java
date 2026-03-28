package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

final class ServletApiTestSupport {
    private ServletApiTestSupport() {
    }

    static TestRequestContext request(String method, String contextPath) {
        return new TestRequestContext(method, contextPath);
    }

    static TestResponseContext response() {
        return new TestResponseContext();
    }

    static final class TestRequestContext {
        private final String method;
        private final String contextPath;
        private final Map<String, String> parameters = new HashMap<>();
        private final TestSessionContext session = new TestSessionContext();
        private boolean createSession;

        private TestRequestContext(String method, String contextPath) {
            this.method = method;
            this.contextPath = contextPath;
        }

        TestRequestContext parameter(String name, String value) {
            parameters.put(name, value);
            return this;
        }

        TestRequestContext authenticated(long customerId) {
            session.attributes.put(AuthSession.AUTH_CUSTOMER_ID, customerId);
            return this;
        }

        HttpServletRequest proxy() {
            InvocationHandler handler = (proxy, methodCall, args) -> switch (methodCall.getName()) {
                case "getMethod" -> method;
                case "getParameter" -> parameters.get(args[0]);
                case "getContextPath" -> contextPath;
                case "getSession" -> {
                    if (args == null || args.length == 0) {
                        createSession = true;
                        yield session.proxy();
                    }
                    boolean create = (Boolean) args[0];
                    if (create) {
                        createSession = true;
                        yield session.proxy();
                    }
                    yield session.attributes.isEmpty() && !createSession ? null : session.proxy();
                }
                default -> defaultValue(methodCall.getReturnType());
            };
            return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(), new Class[]{HttpServletRequest.class}, handler);
        }
    }

    static final class TestResponseContext {
        private String redirectedUrl;
        private boolean committed;

        HttpServletResponse proxy() {
            InvocationHandler handler = (proxy, methodCall, args) -> switch (methodCall.getName()) {
                case "sendRedirect" -> {
                    redirectedUrl = (String) args[0];
                    committed = true;
                    yield null;
                }
                case "isCommitted" -> committed;
                default -> defaultValue(methodCall.getReturnType());
            };
            return (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(), new Class[]{HttpServletResponse.class}, handler);
        }

        String redirectedUrl() {
            return redirectedUrl;
        }

        boolean committed() {
            return committed;
        }
    }

    static final class TestSessionContext {
        private final Map<String, Object> attributes = new HashMap<>();

        HttpSession proxy() {
            InvocationHandler handler = (proxy, methodCall, args) -> switch (methodCall.getName()) {
                case "getAttribute" -> attributes.get(args[0]);
                case "setAttribute" -> {
                    attributes.put((String) args[0], args[1]);
                    yield null;
                }
                case "removeAttribute" -> {
                    attributes.remove(args[0]);
                    yield null;
                }
                default -> defaultValue(methodCall.getReturnType());
            };
            return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(), new Class[]{HttpSession.class}, handler);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == double.class) {
            return 0D;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }
}
