package io.github.phunguy65.bookstore.shared.domain.validation;

public final class FieldValidationException extends IllegalArgumentException {
    private final String fieldName;

    public FieldValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
