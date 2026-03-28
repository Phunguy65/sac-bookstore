package io.github.phunguy65.bookstore.auth.application.service;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class BCryptPasswordHasher implements PasswordHasher {
    private static final int WORK_FACTOR = 12;

    @Override
    public String hash(String rawPassword) {
        String normalized = Require.notBlank(rawPassword, "rawPassword");
        return BCrypt.hashpw(normalized, BCrypt.gensalt(WORK_FACTOR));
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        String normalizedPassword = Require.notBlank(rawPassword, "rawPassword");
        String normalizedHash = Require.notBlank(passwordHash, "passwordHash");
        return BCrypt.checkpw(normalizedPassword, normalizedHash);
    }
}
