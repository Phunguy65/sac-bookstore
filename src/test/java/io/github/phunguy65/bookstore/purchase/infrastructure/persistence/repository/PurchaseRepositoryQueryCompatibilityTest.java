package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseRepositoryQueryCompatibilityTest {
    private static final Pattern FETCH_JOIN_ALIAS = Pattern.compile(
            "join\\s+fetch\\s+\\S+\\s+(?!where\\b|and\\b|or\\b|order\\b|group\\b|having\\b|on\\b)[a-zA-Z]\\w*",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern FETCH_JOIN_WITH_RESULT_LIMIT = Pattern.compile(
            "join\\s+fetch[\\s\\S]*?setMaxResults\\s*\\(",
            Pattern.CASE_INSENSITIVE
    );

    @Test
    void purchaseRepositoryQueriesDoNotAliasFetchJoins() throws IOException {
        Path repositoryRoot = Path.of("src/main/java/io/github/phunguy65/bookstore/purchase/infrastructure/persistence/repository");
        List<String> violations;
        try (Stream<Path> paths = Files.walk(repositoryRoot)) {
            violations = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("Repository.java"))
                    .filter(this::containsAliasedFetchJoin)
                    .map(repositoryRoot::relativize)
                    .map(Path::toString)
                    .toList();
        }

        assertTrue(
                violations.isEmpty(),
                () -> "Purchase repository JPQL fetch joins must not declare aliases for WildFly compatibility: " + violations
        );
    }

    @Test
    void purchaseRepositoryQueriesDoNotLimitFetchedCollections() throws IOException {
        Path repositoryRoot = Path.of("src/main/java/io/github/phunguy65/bookstore/purchase/infrastructure/persistence/repository");
        List<String> violations;
        try (Stream<Path> paths = Files.walk(repositoryRoot)) {
            violations = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("Repository.java"))
                    .filter(this::containsFetchJoinWithResultLimit)
                    .map(repositoryRoot::relativize)
                    .map(Path::toString)
                    .toList();
        }

        assertTrue(
                violations.isEmpty(),
                () -> "Purchase repository JPQL fetch joins must not be combined with setMaxResults when loading cart/order items: " + violations
        );
    }

    private boolean containsAliasedFetchJoin(Path path) {
        try {
            return FETCH_JOIN_ALIAS.matcher(Files.readString(path)).find();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private boolean containsFetchJoinWithResultLimit(Path path) {
        try {
            return FETCH_JOIN_WITH_RESULT_LIMIT.matcher(Files.readString(path)).find();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
