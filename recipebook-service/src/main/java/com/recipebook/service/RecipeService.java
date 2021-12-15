package com.recipebook.service;

import com.recipebook.domain.entities.Recipe;
import com.recipebook.domain.entities.User;
import com.recipebook.domain.exceptions.APIException;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.values.PageResponse;
import com.recipebook.domain.values.RecipeRequest;
import com.recipebook.domain.values.RecipeResponse;
import com.recipebook.repository.RecipeRepository;
import com.recipebook.repository.UserRepository;
import com.recipebook.service.mappers.RecipeMapper;
import com.recipebook.util.SecurityUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author - AvanishKishorPandey
 */

@Service
@Transactional
@Slf4j
public class RecipeService {
    private static final String ENTITY_NAME = "Recipe";
    private static final RecipeMapper RECIPE_MAPPER_INSTANCE = RecipeMapper.INSTANCE;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(final RecipeRepository recipeRepository, final UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<RecipeResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        Page<Recipe> pageResult = recipeRepository.findByUserGuid(SecurityUtils.getCurrentUserLogin(), pageable);
        return PageResponse.of(pageResult, RECIPE_MAPPER_INSTANCE::toResponse);
    }

    public RecipeResponse createRecipe(@NonNull final RecipeRequest createRequest) {
        return Optional.of(createRequest)
                .map(recipeRequest -> {
                    Recipe recipeEntity = RECIPE_MAPPER_INSTANCE.toEntity(recipeRequest, userRepository);
                    recipeEntity = this.recipeRepository.save(recipeEntity);
                    return RECIPE_MAPPER_INSTANCE.toResponse(recipeEntity);
                })
                .orElseThrow(() -> new APIException("Unable to create recipe."));
    }

    public RecipeResponse findByGuid(@NonNull final String guid) {
        return recipeRepository.findOneByGuid(guid)
                .filter(this::hasPermission)
                .map(RECIPE_MAPPER_INSTANCE::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "GUID", guid));
    }

    public RecipeResponse updateRecipe(@NonNull final RecipeRequest updateRequest) {
        return recipeRepository.findOneByGuid(updateRequest.getGuid())
                .filter(this::hasPermission)
                .map(recipe -> {
                    RECIPE_MAPPER_INSTANCE.updateEntity(updateRequest, recipe);
                    this.recipeRepository.save(recipe);
                    return RECIPE_MAPPER_INSTANCE.toResponse(recipe);
                })
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "GUID", updateRequest.getGuid()));
    }

    public void deleteRecipe(@NonNull final String recipeId) {
        recipeRepository.findOneByGuid(recipeId)
                .filter(this::hasPermission)
                .ifPresentOrElse(recipe -> recipeRepository.deleteById(recipe.getId()), () -> {
                    throw new ResourceNotFoundException(ENTITY_NAME, "GUID", recipeId);
                });
    }

    boolean hasPermission(@NonNull final Recipe recipe) {
        String currentLoginUser = SecurityUtils.getCurrentUserLogin();
        User recipeUser = recipe.getUser();
        if(!StringUtils.hasText(currentLoginUser) || !recipeUser.getGuid().equals(currentLoginUser)) {
            throw new AccessDeniedException("You don't have permission to edit/delete this record.");
        }
        return true;
    }
}
