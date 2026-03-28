package io.github.phunguy65.bookstore.auth.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.repository.CustomerRepository;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Optional;

@Stateless
public class AuthApplicationService {
    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private PasswordHasher passwordHasher;

    public AuthApplicationService() {
    }

    public AuthApplicationService(CustomerRepository customerRepository, PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    public LoginResult login(String email, String rawPassword) {
        try {
            Email normalizedEmail = new Email(email);
            String normalizedPassword = Require.notBlank(rawPassword, "rawPassword");
            Optional<Customer> maybeCustomer = customerRepository.findByEmail(normalizedEmail);
            if (maybeCustomer.isEmpty()) {
                return LoginResult.failure("Email hoac mat khau khong dung.");
            }

            Customer customer = maybeCustomer.get();
            if (customer.status() != CustomerStatus.ACTIVE) {
                return LoginResult.failure("Tai khoan hien khong kha dung.");
            }

            boolean matches = passwordHasher.matches(normalizedPassword, customer.passwordHash().value());
            if (!matches) {
                return LoginResult.failure("Email hoac mat khau khong dung.");
            }

            return LoginResult.success(customer);
        } catch (IllegalArgumentException exception) {
            return LoginResult.failure(exception.getMessage());
        } catch (RuntimeException exception) {
            return LoginResult.failure("Dang nhap that bai. Vui long thu lai sau.");
        }
    }

    public RegisterResult register(
            String email,
            String rawPassword,
            String confirmPassword,
            String fullName,
            String phoneNumber
    ) {
        try {
            String normalizedPassword = Require.notBlank(rawPassword, "rawPassword");
            if (confirmPassword == null || !confirmPassword.equals(rawPassword)) {
                return RegisterResult.failure("Xac nhan mat khau khong khop.");
            }

            Email normalizedEmail = new Email(email);
            if (customerRepository.findByEmail(normalizedEmail).isPresent()) {
                return RegisterResult.failure("Email da duoc su dung.");
            }

            Instant now = Instant.now();
            Customer customer = new Customer(
                    null,
                    normalizedEmail,
                    new PasswordHash(passwordHasher.hash(normalizedPassword)),
                    new FullName(fullName),
                    new PhoneNumber(phoneNumber),
                    CustomerStatus.ACTIVE,
                    0L,
                    now,
                    now
            );

            Customer savedCustomer = customerRepository.save(customer);
            return RegisterResult.success(savedCustomer);
        } catch (IllegalArgumentException exception) {
            return RegisterResult.failure(exception.getMessage());
        } catch (RuntimeException exception) {
            return RegisterResult.failure("Dang ky that bai. Vui long thu lai sau.");
        }
    }
}
