package io.github.phunguy65.bookstore.auth.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoModeConfigTest {
    @Test
    void returnsDefaultsFromMissingEnvironmentValues() {
        DemoModeConfig config = new DemoModeConfig(key -> null);

        assertFalse(config.isEnabled());
        assertNull(config.getEmail());
        assertNull(config.getPassword());
    }

    @Test
    void readsDemoModeValuesFromEnvironmentProvider() {
        Map<String, String> env = Map.of(
                "BOOKSTORE_DEMO_MODE", "true",
                "BOOKSTORE_DEMO_EMAIL", "dev@bookstore.local",
                "BOOKSTORE_DEMO_PASSWORD", "dev123456"
        );
        DemoModeConfig config = new DemoModeConfig(env::get);

        assertTrue(config.isEnabled());
        assertEquals("dev@bookstore.local", config.getEmail());
        assertEquals("dev123456", config.getPassword());
    }
}
