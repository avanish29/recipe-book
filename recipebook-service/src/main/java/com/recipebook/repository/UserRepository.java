package com.recipebook.repository;

import com.recipebook.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddressIgnoreCase(final String emailAddress);

    boolean existsByEmailAddressIgnoreCase(final String emailAddress);

    Optional<User> findOneByGuid(final String uuid);
}