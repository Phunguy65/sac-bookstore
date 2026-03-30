package io.github.phunguy65.bookstore.auth.infrastructure.config;

import io.github.phunguy65.bookstore.auth.application.service.DemoModeSettings;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Function;

@ApplicationScoped
public class DemoModeConfig implements DemoModeSettings {
    private static final String DEMO_MODE_ENV = "BOOKSTORE_DEMO_MODE";
    private static final String DEMO_EMAIL_ENV = "BOOKSTORE_DEMO_EMAIL";
    private static final String DEMO_PASSWORD_ENV = "BOOKSTORE_DEMO_PASSWORD";
    private final Function<String, String> envReader;

    public DemoModeConfig() {
        this(System::getenv);
    }

    DemoModeConfig(Function<String, String> envReader) {
        this.envReader = envReader;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.parseBoolean(envReader.apply(DEMO_MODE_ENV));
    }

    @Override
    public String getEmail() {
        return envReader.apply(DEMO_EMAIL_ENV);
    }

    @Override
    public String getPassword() {
        return envReader.apply(DEMO_PASSWORD_ENV);
    }
}
