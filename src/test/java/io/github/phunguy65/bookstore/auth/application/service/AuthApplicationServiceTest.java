package io.github.phunguy65.bookstore.auth.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.repository.CustomerRepository;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthApplicationServiceTest {
    @Test
    void registersNewActiveCustomerWithHashedPassword() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        StubPasswordHasher passwordHasher = new StubPasswordHasher();
        AuthApplicationService service = new AuthApplicationService(customerRepository, passwordHasher);

        RegisterResult result = service.register(
                "Reader@Example.com",
                "secret-123",
                "secret-123",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertTrue(result.isSuccess());
        assertEquals("reader@example.com", result.getCustomer().email().value());
        assertNotEquals("secret-123", result.getCustomer().passwordHash().value());
        assertEquals(CustomerStatus.ACTIVE, result.getCustomer().status());
        assertEquals("HASH::secret-123", result.getCustomer().passwordHash().value());
    }

    @Test
    void rejectsDuplicateEmailDuringRegistration() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        StubPasswordHasher passwordHasher = new StubPasswordHasher();
        customerRepository.save(customer("reader@example.com", "HASH::existing", CustomerStatus.ACTIVE));
        AuthApplicationService service = new AuthApplicationService(customerRepository, passwordHasher);

        RegisterResult result = service.register(
                "reader@example.com",
                "secret-123",
                "secret-123",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("Email da duoc su dung.", result.getErrorMessage());
    }

    @Test
    void rejectsMismatchedPasswordConfirmation() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "reader@example.com",
                "secret-123",
                "other-secret",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("Xac nhan mat khau khong khop.", result.getErrorMessage());
    }

    @Test
    void logsInExistingActiveCustomer() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerRepository.save(customer("reader@example.com", "HASH::secret-123", CustomerStatus.ACTIVE));
        AuthApplicationService service = new AuthApplicationService(customerRepository, new StubPasswordHasher());

        LoginResult result = service.login("reader@example.com", "secret-123");

        assertTrue(result.isSuccess());
        assertEquals("reader@example.com", result.getCustomer().email().value());
    }

    @Test
    void rejectsInvalidCredentials() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerRepository.save(customer("reader@example.com", "HASH::secret-123", CustomerStatus.ACTIVE));
        AuthApplicationService service = new AuthApplicationService(customerRepository, new StubPasswordHasher());

        LoginResult result = service.login("reader@example.com", "wrong-password");

        assertFalse(result.isSuccess());
        assertEquals("Email hoac mat khau khong dung.", result.getErrorMessage());
    }

    @Test
    void rejectsInactiveAccount() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerRepository.save(customer("reader@example.com", "HASH::secret-123", CustomerStatus.INACTIVE));
        AuthApplicationService service = new AuthApplicationService(customerRepository, new StubPasswordHasher());

        LoginResult result = service.login("reader@example.com", "secret-123");

        assertFalse(result.isSuccess());
        assertEquals("Tai khoan hien khong kha dung.", result.getErrorMessage());
    }

    @Test
    void rejectsBlankEmailDuringLogin() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        LoginResult result = service.login(" ", "secret-123");

        assertFalse(result.isSuccess());
        assertEquals("email must not be blank", result.getErrorMessage());
    }

    @Test
    void rejectsBlankPasswordDuringLogin() {
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerRepository.save(customer("reader@example.com", "HASH::secret-123", CustomerStatus.ACTIVE));
        AuthApplicationService service = new AuthApplicationService(customerRepository, new StubPasswordHasher());

        LoginResult result = service.login("reader@example.com", " ");

        assertFalse(result.isSuccess());
        assertEquals("rawPassword must not be blank", result.getErrorMessage());
    }

    @Test
    void rejectsInvalidEmailDuringRegistration() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "not-an-email",
                "secret-123",
                "secret-123",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("email must be a valid email address", result.getErrorMessage());
    }

    @Test
    void rejectsBlankPasswordDuringRegistration() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "reader@example.com",
                " ",
                " ",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("rawPassword must not be blank", result.getErrorMessage());
    }

    @Test
    void rejectsBlankFullNameDuringRegistration() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "reader@example.com",
                "secret-123",
                "secret-123",
                " ",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("fullName must not be blank", result.getErrorMessage());
    }

    @Test
    void rejectsInvalidPhoneNumberDuringRegistration() {
        AuthApplicationService service = new AuthApplicationService(new InMemoryCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "reader@example.com",
                "secret-123",
                "secret-123",
                "Nguyen Van Doc",
                "123"
        );

        assertFalse(result.isSuccess());
        assertEquals("phoneNumber must be a valid phone number", result.getErrorMessage());
    }

    @Test
    void returnsFriendlyMessageWhenLoginRepositoryFails() {
        AuthApplicationService service = new AuthApplicationService(new FailingCustomerRepository(), new StubPasswordHasher());

        LoginResult result = service.login("reader@example.com", "secret-123");

        assertFalse(result.isSuccess());
        assertEquals("Dang nhap that bai. Vui long thu lai sau.", result.getErrorMessage());
    }

    @Test
    void returnsFriendlyMessageWhenRegisterPersistenceFails() {
        AuthApplicationService service = new AuthApplicationService(new FailingCustomerRepository(), new StubPasswordHasher());

        RegisterResult result = service.register(
                "reader@example.com",
                "secret-123",
                "secret-123",
                "Nguyen Van Doc",
                "0901234567"
        );

        assertFalse(result.isSuccess());
        assertEquals("Dang ky that bai. Vui long thu lai sau.", result.getErrorMessage());
    }

    private static Customer customer(String email, String passwordHash, CustomerStatus status) {
        Instant now = Instant.parse("2026-03-27T00:00:00Z");
        return new Customer(
                new CustomerId(1L),
                new Email(email),
                new PasswordHash(passwordHash),
                new FullName("Nguyen Van Doc"),
                new PhoneNumber("0901234567"),
                status,
                0L,
                now,
                now
        );
    }

    private static final class StubPasswordHasher implements PasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "HASH::" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return hash(rawPassword).equals(passwordHash);
        }
    }

    private static final class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<String, Customer> customersByEmail = new HashMap<>();
        private long nextId = 1L;

        @Override
        public Optional<Customer> findById(CustomerId id) {
            return customersByEmail.values().stream()
                    .filter(customer -> customer.id() != null && customer.id().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<Customer> findByEmail(Email email) {
            return Optional.ofNullable(customersByEmail.get(email.value()));
        }

        @Override
        public Customer save(Customer customer) {
            Customer persisted = customer.id() != null ? customer : new Customer(
                    new CustomerId(nextId++),
                    customer.email(),
                    customer.passwordHash(),
                    customer.fullName(),
                    customer.phoneNumber(),
                    customer.status(),
                    customer.version(),
                    customer.createdAt(),
                    customer.updatedAt()
            );
            customersByEmail.put(persisted.email().value(), persisted);
            return persisted;
        }
    }

    private static final class FailingCustomerRepository implements CustomerRepository {
        @Override
        public Optional<Customer> findById(CustomerId id) {
            throw new IllegalStateException("db unavailable");
        }

        @Override
        public Optional<Customer> findByEmail(Email email) {
            throw new IllegalStateException("db unavailable");
        }

        @Override
        public Customer save(Customer customer) {
            throw new IllegalStateException("db unavailable");
        }
    }
}
