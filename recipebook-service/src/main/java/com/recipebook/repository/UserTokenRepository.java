package com.recipebook.repository;

import com.recipebook.domain.entities.UserToken;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author - AvanishKishorPandey
 */

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long>{
	Optional<UserToken> findByToken(@NonNull final String token);
}
