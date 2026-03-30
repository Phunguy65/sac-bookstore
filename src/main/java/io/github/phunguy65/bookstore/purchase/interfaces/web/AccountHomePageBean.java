package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import io.github.phunguy65.bookstore.purchase.application.service.AccountCatalogApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CartActionResult;
import io.github.phunguy65.bookstore.shared.domain.validation.FieldValidationException;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Named
@Dependent
public class AccountHomePageBean {
    private final AccountCatalogApplicationService accountCatalogApplicationService;

    @Inject
    public AccountHomePageBean(AccountCatalogApplicationService accountCatalogApplicationService) {
        this.accountCatalogApplicationService = accountCatalogApplicationService;
    }

    public AccountHomePageModel handle(HttpServletRequest request, HttpServletResponse response) {
        String errorMessage = null;
        String infoMessage = null;
        Long lineErrorBookId = null;
        String lineQuantityError = null;
        Long submittedBookId = tryParseLong(trimToEmpty(request.getParameter("bookId")));
        String submittedQuantity = trimToEmpty(request.getParameter("quantity"));

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            CartActionResult result;
            try {
                result = handleAddToCart(request);
            } catch (IllegalArgumentException ex) {
                result = requestFailure(ex);
            }

            if (result.isSuccess()) {
                infoMessage = "Da them sach vao gio hang.";
                submittedQuantity = "1";
            } else {
                lineQuantityError = result.getFieldError("quantity");
                if (lineQuantityError != null) {
                    lineErrorBookId = submittedBookId;
                    errorMessage = null;
                } else {
                    errorMessage = result.getErrorMessage();
                }
            }
        }

        return new AccountHomePageModel(
                accountCatalogApplicationService.getActiveBooks(),
                errorMessage,
                infoMessage,
                lineErrorBookId,
                lineQuantityError,
                submittedBookId,
                submittedQuantity
        );
    }

    private CartActionResult handleAddToCart(HttpServletRequest request) {
        long bookId = parseLong(request.getParameter("bookId"));
        int quantity = parseInt(request.getParameter("quantity"));
        return accountCatalogApplicationService.addBook(AuthSession.getCustomerId(request), bookId, quantity);
    }

    private CartActionResult requestFailure(IllegalArgumentException ex) {
        String message = ex.getMessage();
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        if (ex instanceof FieldValidationException fieldValidationException) {
            fieldErrors.put(fieldValidationException.getFieldName(), message);
        }
        return CartActionResult.failure(message, fieldErrors);
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

    private Long tryParseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
