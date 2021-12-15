package com.recipebook.service.mappers;

import com.recipebook.domain.entities.Ingredient;
import com.recipebook.domain.entities.Recipe;
import com.recipebook.domain.entities.RecipeIngredient;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.values.RecipeRequest;
import com.recipebook.domain.values.RecipeResponse;
import com.recipebook.repository.UserRepository;
import com.recipebook.util.SecurityUtils;
import lombok.NonNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author - AvanishKishorPandey
 */

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "recipeIngredient", source = "ingredients", qualifiedByName = "listToRecipeIngredient")
    Recipe toEntity(RecipeRequest recipeRequest, @Context UserRepository userRepository);

    @Mapping(target = "uuid", source = "guid")
    @Mapping(target = "isVegetarian", source = "vegetarian")
    @Mapping(target = "createdAt", source = "createdOn", dateFormat = "dd‐MM‐yyyy HH:mm")
    @Mapping(target = "ingredients", source = "recipeIngredient", qualifiedByName = "recipeIngredientToList")
    RecipeResponse toResponse(final Recipe recipe);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "recipeIngredient", source = "ingredients", qualifiedByName = "listToRecipeIngredient")
    void updateEntity(final RecipeRequest recipeRequest, @MappingTarget Recipe recipeEntity);

    @AfterMapping
    default void mapUser(@MappingTarget Recipe recipeEntity, @Context UserRepository userRepository) {
        Optional.ofNullable(SecurityUtils.getCurrentUserLogin())
                .flatMap(userRepository::findOneByGuid)
                .ifPresentOrElse(recipeEntity::setUser, () -> {
                    throw new ResourceNotFoundException("User not found in context");
                });
    }

    @Named("listToRecipeIngredient")
    default RecipeIngredient mapSetToRecipeIngredient(@NonNull final Set<String> ingredients) {
        RecipeIngredient recipeIngredient = null;
        if(!CollectionUtils.isEmpty(ingredients)) {
            List<Ingredient> ingredientList = ingredients.stream().map(Ingredient::new).collect(Collectors.toList());
            recipeIngredient = new RecipeIngredient(ingredientList);
        }
        return recipeIngredient;
    }

    @Named("recipeIngredientToList")
    default Set<String> mapRecipeIngredientToSet(@NonNull final RecipeIngredient recipeIngredient) {
        return recipeIngredient.getIngredients().stream().map(Ingredient::getName).collect(Collectors.toSet());
    }
}
