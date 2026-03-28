package io.github.phunguy65.bookstore.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JspCompatibilityTest {
    private static final Pattern FOREACH_VAR_SCRIPTLET = Pattern.compile("<%\\s*for\\s*\\(\\s*var\\b");

    @Test
    void jspScriptletForeachLoopsUseExplicitTypes() throws IOException {
        Path webappRoot = Path.of("src/main/webapp");
        List<String> violations;
        try (Stream<Path> paths = Files.walk(webappRoot)) {
            violations = paths
                    .filter(Files::isRegularFile)
                    .filter(this::isJspFile)
                    .filter(this::containsForeachVarScriptlet)
                    .map(webappRoot::relativize)
                    .map(Path::toString)
                    .toList();
        }

        assertTrue(
                violations.isEmpty(),
                () -> "JSP scriptlet foreach loops must use explicit types for WildFly compatibility: " + violations
        );
    }

    private boolean isJspFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".jsp") || fileName.endsWith(".jspf");
    }

    private boolean containsForeachVarScriptlet(Path path) {
        try {
            return FOREACH_VAR_SCRIPTLET.matcher(Files.readString(path)).find();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
