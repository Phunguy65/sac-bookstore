package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import io.github.phunguy65.bookstore.purchase.application.service.CartActionResult;
import io.github.phunguy65.bookstore.purchase.application.service.CartApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CartView;
import io.github.phunguy65.bookstore.shared.domain.validation.FieldValidationException;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Named
@Dependent
public class CartPageBean {
    private final CartApplicationService cartApplicationService;

    @Inject
    public CartPageBean(CartApplicationService cartApplicationService) {
        this.cartApplicationService = cartApplicationService;
    }

    public CartPageModel handle(HttpServletRequest request, HttpServletResponse response) {
        var customerId = AuthSession.getCustomerId(request);
        String errorMessage = null;
        String infoMessage = mapInfoMessage(request.getParameter("info"));
        Long lineErrorBookId = null;
        String lineQuantityError = null;

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String action = trimToEmpty(request.getParameter("action"));
            Long submittedBookId = null;
            CartActionResult result;
            try {
                result = handleMutation(request);
                String rawBookId = trimToEmpty(request.getParameter("bookId"));
                if (!rawBookId.isEmpty()) {
                    submittedBookId = Long.parseLong(rawBookId);
                }
            } catch (IllegalArgumentException ex) {
                result = requestFailure(action, ex);
            }
            if (result.isSuccess()) {
                infoMessage = "Gio hang da duoc cap nhat.";
            } else {
                String message = result.getErrorMessage();
                errorMessage = message;
                if ("update".equals(action)) {
                    lineQuantityError = result.getFieldError("quantity");
                    if (lineQuantityError != null) {
                        lineErrorBookId = submittedBookId;
                    }
                }
            }
        }

        CartView cart = cartApplicationService.getCart(customerId);
        return new CartPageModel(cart, errorMessage, infoMessage, lineErrorBookId, lineQuantityError);
    }

    private String mapInfoMessage(String value) {
        if ("emptyCheckout".equals(trimToEmpty(value))) {
            return "Gio hang dang trong. Vui long them sach truoc khi thanh toan.";
        }
        return null;
    }

    private CartActionResult requestFailure(String action, IllegalArgumentException ex) {
        String message = ex.getMessage();
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        if (ex instanceof FieldValidationException fieldValidationException) {
            fieldErrors.put(fieldValidationException.getFieldName(), message);
            if ("update".equals(action) && "quantity".equals(fieldValidationException.getFieldName())) {
                fieldErrors.put("lineQuantity", message);
            }
        }
        return CartActionResult.failure(message, fieldErrors);
    }

    private CartActionResult handleMutation(HttpServletRequest request) {
        String action = trimToEmpty(request.getParameter("action"));
        return switch (action) {
            case "update" -> cartApplicationService.updateQuantity(AuthSession.getCustomerId(request), parseLong(request.getParameter("bookId")), parseInt(request.getParameter("quantity")));
            case "remove" -> cartApplicationService.removeBook(AuthSession.getCustomerId(request), parseLong(request.getParameter("bookId")));
            default -> CartActionResult.failure("Hanh dong gio hang khong hop le.");
        };
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(trimToEmpty(value));
        } catch (NumberFormatException ex) {
            throw new FieldValidationException("quantity", "So luong khong hop le.");
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(trimToEmpty(value));
        } catch (NumberFormatException ex) {
            throw new FieldValidationException("bookId", "Ma sach khong hop le.");
        }
    }
}
