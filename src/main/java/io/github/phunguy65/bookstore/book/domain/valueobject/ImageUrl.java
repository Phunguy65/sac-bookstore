package io.github.phunguy65.bookstore.book.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

import java.net.URI;

public record ImageUrl(String value) implements Serializable {
    public ImageUrl {
        String normalized = Require.notBlank(value, "imageUrl");
        Require.maxLength(normalized, 500, "imageUrl");
        URI uri = URI.create(normalized);
        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("imageUrl must be an absolute http/https URL");
        }
        value = normalized;
    }
}
