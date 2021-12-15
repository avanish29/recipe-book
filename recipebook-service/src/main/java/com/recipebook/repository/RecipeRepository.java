package com.recipebook.repository;

import com.recipebook.domain.entities.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author - AvanishKishorPandey
 */

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByUserGuid(String guid, Pageable pageable);

    Optional<Recipe> findOneByGuid(String guid);
}
