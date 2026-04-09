package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CartActionResult;
import io.github.phunguy65.bookstore.purchase.application.service.CartApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CartView;
import io.github.phunguy65.bookstore.shared.domain.validation.FieldValidationException;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class CartPageBean implements CartPage {
    private CartApplicationService cartApplicationService;

    public CartPageBean() {
    }

    @Inject
    public CartPageBean(CartApplicationService cartApplicationService) {
        this.cartApplicationService = cartApplicationService;
    }

    @Override
    public CartPageResult handle(CartPageRequest request) {
        var customerId = CustomerId.DEFAULT_CUSTOMER;
        String errorMessage = null;
        String infoMessage = mapInfoMessage(request.infoParam());
        Long lineErrorBookId = null;
        String lineQuantityError = null;

        if ("POST".equalsIgnoreCase(request.method())) {
            String action = trimToEmpty(request.action());
            Long submittedBookId = null;
            CartActionResult result;
            try {
                result = handleMutation(request);
                String rawBookId = trimToEmpty(request.bookId());
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
        CartPageModel model = new CartPageModel(cart, errorMessage, infoMessage, lineErrorBookId, lineQuantityError);
        return new CartPageResult(PageAction.RENDER, model);
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

    private CartActionResult handleMutation(CartPageRequest request) {
        String action = trimToEmpty(request.action());
        return switch (action) {
            case "update" -> cartApplicationService.updateQuantity(CustomerId.DEFAULT_CUSTOMER, parseLong(request.bookId()), parseInt(request.quantity()));
            case "remove" -> cartApplicationService.removeBook(CustomerId.DEFAULT_CUSTOMER, parseLong(request.bookId()));
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
