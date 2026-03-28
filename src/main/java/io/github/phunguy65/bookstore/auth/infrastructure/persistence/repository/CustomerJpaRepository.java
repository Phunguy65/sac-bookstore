package io.github.phunguy65.bookstore.auth.infrastructure.persistence.repository;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.repository.CustomerRepository;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.mapper.CustomerEntityMapper;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class CustomerJpaRepository implements CustomerRepository {
    @PersistenceContext(unitName = "bookstore")
    private EntityManager entityManager;

    @Override
    public Optional<Customer> findById(CustomerId id) {
        CustomerEntity entity = entityManager.find(CustomerEntity.class, id.value());
        return entity == null ? Optional.empty() : Optional.of(CustomerEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        List<CustomerEntity> results = entityManager.createQuery(
                        "select c from CustomerEntity c where c.email = :email",
                        CustomerEntity.class
                )
                .setParameter("email", email.value())
                .setMaxResults(1)
                .getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(CustomerEntityMapper.toDomain(results.getFirst()));
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntityMapper.toEntity(customer);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            entityManager.flush();
            return CustomerEntityMapper.toDomain(entity);
        }

        CustomerEntity merged = entityManager.merge(entity);
        entityManager.flush();
        return CustomerEntityMapper.toDomain(merged);
    }
}
