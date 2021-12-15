package com.recipebook.service;

import com.recipebook.domain.entities.Ingredient;
import com.recipebook.domain.entities.Recipe;
import com.recipebook.domain.entities.RecipeIngredient;
import com.recipebook.domain.entities.User;
import com.recipebook.domain.values.RecipeRequest;
import com.recipebook.domain.values.RecipeResponse;
import com.recipebook.domain.values.UserPrincipal;
import com.recipebook.repository.RecipeRepository;
import com.recipebook.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author - AvanishKishorPandey
 */

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    private RecipeService recipeService;

    @BeforeEach
    public void setup() {
        recipeService = new RecipeService(recipeRepository, userRepository);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(123L, UUID.randomUUID().toString(), "First", "Last",  null)
                , null, null));
    }

    @Test
    void testCreateRecipe() {
        final RecipeRequest createRequest = mockRecipeRequest();
        final Recipe recipe = newRecipeEntity();

        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
        Mockito.when(userRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(mockUserEntity()));

        RecipeResponse recipeResponse = recipeService.createRecipe(createRequest);

        Assertions.assertThat(recipeResponse).isNotNull();
        Assertions.assertThat(recipeResponse.getUuid()).isNotNull().isEqualTo(recipe.getGuid());
    }

   /* @Test
    void testUpdateRecipe() {
        final RecipeRequest createRequest = mockRecipeRequest();
        final Recipe recipe = newRecipeEntity();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(recipe));
        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
        Mockito.when(userRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(mockUserEntity()));

        RecipeResponse recipeResponse = recipeService.createRecipe(createRequest);

        Assertions.assertThat(recipeResponse).isNotNull();
        Assertions.assertThat(recipeResponse.getUuid()).isNotNull().isEqualTo(recipe.getGuid());
    }*/

    private User mockUserEntity() {
        User userEntity = new User();
        userEntity.setId(123L);
        userEntity.setEmailAddress("first.last@email.com");
        userEntity.setLastName("Last");
        userEntity.setFirstName("First");
        return userEntity;
    }

    private Recipe newRecipeEntity() {
        Recipe recipeEntity = new Recipe();
        recipeEntity.setId(1234L);
        recipeEntity.setName("Test");
        recipeEntity.setVegetarian(true);
        recipeEntity.setSuitableFor(2);
        List<Ingredient> ingredients = Stream.of("Test Ingredient1", "Test Ingredient2", "Test Ingredient3")
                .map(Ingredient::new).collect(Collectors.toList());
        recipeEntity.setRecipeIngredient(new RecipeIngredient(ingredients));
        recipeEntity.setCookingInstruction("Test Cooking Instruction");
        return recipeEntity;
    }

    private RecipeRequest mockRecipeRequest() {
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setName("Test");
        recipeRequest.setVegetarian(true);
        recipeRequest.setSuitableFor(2);
        recipeRequest.setIngredients(Set.of("Test Ingredient1", "Test Ingredient2", "Test Ingredient3"));
        recipeRequest.setCookingInstruction("Test Cooking Instruction");
        return recipeRequest;
    }
}
