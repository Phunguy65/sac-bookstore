package io.github.phunguy65.bookstore.auth.infrastructure.persistence.repository;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.auth.domain.repository.AddressRepository;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.AddressEntity;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.mapper.AddressEntityMapper;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class AddressJpaRepository implements AddressRepository {
    @PersistenceContext(unitName = "bookstore")
    private EntityManager entityManager;

    @Override
    public List<Address> findByCustomerId(CustomerId customerId) {
        return entityManager.createQuery(
                        "select a from AddressEntity a where a.customer.id = :customerId order by a.isDefault desc, a.createdAt desc",
                        AddressEntity.class
                )
                .setParameter("customerId", customerId.value())
                .getResultList()
                .stream()
                .map(AddressEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Address> findDefaultByCustomerId(CustomerId customerId) {
        List<AddressEntity> results = entityManager.createQuery(
                        "select a from AddressEntity a where a.customer.id = :customerId and a.isDefault = true",
                        AddressEntity.class
                )
                .setParameter("customerId", customerId.value())
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(AddressEntityMapper.toDomain(results.getFirst()));
    }

    @Override
    public Optional<Address> findById(AddressId addressId) {
        AddressEntity entity = entityManager.find(AddressEntity.class, addressId.value());
        return entity == null ? Optional.empty() : Optional.of(AddressEntityMapper.toDomain(entity));
    }

    @Override
    public Address save(Address address) {
        if (address.isDefault()) {
            entityManager.createQuery(
                            "update AddressEntity a set a.isDefault = false where a.customer.id = :customerId and (:addressId is null or a.id <> :addressId)"
                    )
                    .setParameter("customerId", address.customerId().value())
                    .setParameter("addressId", address.id() == null ? null : address.id().value())
                    .executeUpdate();
        }

        AddressEntity entity = AddressEntityMapper.toEntity(address);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return AddressEntityMapper.toDomain(entity);
        }

        AddressEntity merged = entityManager.merge(entity);
        return AddressEntityMapper.toDomain(merged);
    }
}
