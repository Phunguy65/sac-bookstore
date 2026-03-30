package io.github.phunguy65.bookstore.auth.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoModeConfigurationAlignmentTest {
    @Test
    void sharedDemoEnvFileContainsRuntimeAndSeedValues() throws IOException {
        String envFile = Files.readString(Path.of("docker/wildfly/demo-mode.env"));

        assertTrue(envFile.contains("BOOKSTORE_DEMO_EMAIL=dev@bookstore.local"));
        assertTrue(envFile.contains("BOOKSTORE_DEMO_PASSWORD=dev123456"));
        assertTrue(envFile.contains("BOOKSTORE_DEMO_PASSWORD_HASH="));
    }

    @Test
    void buildAndRuntimeConfigLoadSameDemoDefaultsFile() throws IOException {
        String buildFile = Files.readString(Path.of("build.gradle.kts"));
        String dockerfile = Files.readString(Path.of("Dockerfile"));
        String entrypoint = Files.readString(Path.of("docker/wildfly/entrypoint.sh"));

        assertTrue(buildFile.contains("loadEnvDefaults(\"docker/wildfly/demo-mode.env\")"));
        assertTrue(dockerfile.contains("COPY docker/wildfly/demo-mode.env docker/wildfly/demo-mode.env"));
        assertTrue(dockerfile.contains("COPY docker/wildfly/demo-mode.env /opt/jboss/wildfly/customization/demo-mode.env"));
        assertTrue(entrypoint.contains(". /opt/jboss/wildfly/customization/demo-mode.env"));
    }

    @Test
    void flywaySeedStillUsesDemoPlaceholders() throws IOException {
        String migration = Files.readString(Path.of("src/main/resources/db/dev/V2__seed_demo_books_and_dev_account.sql"));

        assertTrue(migration.contains("${demoEmail}"));
        assertTrue(migration.contains("${demoPasswordHash}"));
    }
}
