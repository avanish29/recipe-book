package com.recipebook.service;

import com.recipebook.domain.entities.Ingredient;
import com.recipebook.domain.entities.Recipe;
import com.recipebook.domain.entities.RecipeIngredient;
import com.recipebook.domain.entities.User;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.values.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        recipeService = Mockito.spy(recipeService);
    }

    @Test
    void testFindAll() {
        final Recipe recipe = newRecipeEntity();

        Mockito.when(recipeRepository.findByUserGuid(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(recipe)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), recipe.getUser().getGuid(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        PageResponse<RecipeResponse> pageResponse =  recipeService.findAll(0, 10);

        Assertions.assertThat(pageResponse).isNotNull();
        Assertions.assertThat(pageResponse.getCurrentPage()).isZero();
        Assertions.assertThat(pageResponse.getTotalItems()).isEqualTo(1);
        Assertions.assertThat(pageResponse.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(pageResponse.getContents().get(0).getUuid()).isEqualTo(recipe.getGuid());
    }

    @Test
    void testCreateRecipe() {
        final RecipeRequest createRequest = mockRecipeRequest();
        final Recipe recipe = newRecipeEntity();

        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
        Mockito.when(userRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(mockUserEntity()));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), recipe.getUser().getGuid(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        RecipeResponse recipeResponse = recipeService.createRecipe(createRequest);

        Assertions.assertThat(recipeResponse).isNotNull();
        Assertions.assertThat(recipeResponse.getUuid()).isNotNull().isEqualTo(recipe.getGuid());
    }

    @Test
    void testUpdateRecipe() {
        final Recipe recipe = newRecipeEntity();
        final RecipeRequest updateRequest = mockRecipeRequest();
        updateRequest.setGuid(recipe.getGuid());

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), recipe.getUser().getGuid(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(recipe));
        Mockito.when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);

        RecipeResponse recipeResponse = recipeService.updateRecipe(updateRequest);

        Assertions.assertThat(recipeResponse).isNotNull();
        Assertions.assertThat(recipeResponse.getUuid()).isNotNull().isEqualTo(recipe.getGuid());
    }

    @Test
    void testUpdateRecipeWhenResourceNotAvailable() {
        final RecipeRequest updateRequest = mockRecipeRequest();
        updateRequest.setGuid(UUID.randomUUID().toString());

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Recipe not found with GUID : '%s'", updateRequest.getGuid()));
    }

    @Test
    void testFindByGuid() {
        final Recipe recipe = newRecipeEntity();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(recipe));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), recipe.getUser().getGuid(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        RecipeResponse recipeResponse = recipeService.findByGuid(recipe.getGuid());

        Assertions.assertThat(recipeResponse).isNotNull();
        Assertions.assertThat(recipeResponse.getUuid()).isNotNull().isEqualTo(recipe.getGuid());
    }

    @Test
    void testFindByGuidWhenResourceNotPresent() {
        final String recipeGuid = UUID.randomUUID().toString();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.findByGuid(recipeGuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Recipe not found with GUID : '%s'", recipeGuid));
    }

    @Test
    void testDeleteRecipe() {
        final Recipe recipe  = newRecipeEntity();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(recipe));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), recipe.getUser().getGuid(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        this.recipeService.deleteRecipe(recipe.getGuid());

        Mockito.verify(this.recipeService, Mockito.times(1)).deleteRecipe(recipe.getGuid());
    }

    @Test
    void testDeleteRecipeWhenUserDoesNotHavePermission() {
        final Recipe recipe  = newRecipeEntity();
        final String recipeGuid = recipe.getGuid();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(recipe));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(recipe.getUser().getId(), UUID.randomUUID().toString(), recipe.getUser().getFirstName(), recipe.getUser().getLastName(),  null)
                , null, null));

        assertThatThrownBy(() -> recipeService.deleteRecipe(recipeGuid))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You don't have permission to edit/delete this record.");
    }

    @Test
    void testDeleteRecipeWhenResourceNotAvailable() {
        final String recipeGuid  = UUID.randomUUID().toString();

        Mockito.when(recipeRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe(recipeGuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Recipe not found with GUID : '%s'", recipeGuid));
    }

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
        recipeEntity.setUser(mockUserEntity());
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
