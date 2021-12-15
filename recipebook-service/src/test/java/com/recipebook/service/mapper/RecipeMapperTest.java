package com.recipebook.service.mapper;

import com.recipebook.domain.entities.Ingredient;
import com.recipebook.domain.entities.Recipe;
import com.recipebook.domain.entities.RecipeIngredient;
import com.recipebook.domain.entities.User;
import com.recipebook.domain.values.RecipeRequest;
import com.recipebook.domain.values.RecipeResponse;
import com.recipebook.domain.values.UserPrincipal;
import com.recipebook.repository.UserRepository;
import com.recipebook.service.mappers.RecipeMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeMapperTest {
    private final RecipeMapper recipeMapper = RecipeMapper.INSTANCE;

    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findOneByGuid(Mockito.anyString())).thenReturn(Optional.of(mockUserEntity()));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(123L, UUID.randomUUID().toString(), "First", "Last",  null)
                , null, null));
    }

    @Test
    void testRecipeRequestToEntity() {
        RecipeRequest recipeRequest = newRecipeRequest();
        Recipe resultEntity =  recipeMapper.toEntity(recipeRequest, userRepository);

        Assertions.assertThat(resultEntity).isNotNull();
        Assertions.assertThat(resultEntity.getGuid()).isNotNull();
        Assertions.assertThat(resultEntity.getId()).isNull();
        Assertions.assertThat(resultEntity.getSuitableFor()).isNotNull().isEqualTo(recipeRequest.getSuitableFor());
        Assertions.assertThat(resultEntity.getName()).isNotNull().isEqualTo(recipeRequest.getName());
        Assertions.assertThat(resultEntity.getCookingInstruction()).isNotNull().isEqualTo(recipeRequest.getCookingInstruction());
        Assertions.assertThat(resultEntity.getVegetarian()).isEqualTo(recipeRequest.isVegetarian());
        Assertions.assertThat(resultEntity.getRecipeIngredient()).isNotNull();
        Assertions.assertThat(resultEntity.getRecipeIngredient().getIngredients()).isNotNull().hasSize(3);
    }

    @Test
    void testRecipeRequestToEntityWhenRequestIsNull() {
        Recipe resultEntity = recipeMapper.toEntity(null, userRepository);
        Assertions.assertThat(resultEntity).isNull();
    }

    @Test
    void testRecipeToResponse() {
        Recipe recipeEntity = newRecipeEntity();
        RecipeResponse resultEntity =  recipeMapper.toResponse(recipeEntity);

        Assertions.assertThat(resultEntity).isNotNull();
        Assertions.assertThat(resultEntity.getUuid()).isNotNull();
        Assertions.assertThat(resultEntity.getSuitableFor()).isEqualTo(recipeEntity.getSuitableFor());
        Assertions.assertThat(resultEntity.getName()).isNotNull().isEqualTo(recipeEntity.getName());
        Assertions.assertThat(resultEntity.getCookingInstruction()).isNotNull().isEqualTo(recipeEntity.getCookingInstruction());
        Assertions.assertThat(resultEntity.isVegetarian()).isEqualTo(recipeEntity.getVegetarian());
        Assertions.assertThat(resultEntity.getIngredients()).isNotNull().hasSize(3);
    }

    @Test
    void testRecipeToResponseWhenEntityIsNull() {
        RecipeResponse resultEntity = recipeMapper.toResponse(null);
        Assertions.assertThat(resultEntity).isNull();
    }

    @Test
    void testUpdateEntity() {
        Recipe recipeEntity = newRecipeEntity();
        RecipeRequest recipeRequest = newRecipeRequest();
        recipeMapper.updateEntity(recipeRequest, recipeEntity);

        Assertions.assertThat(recipeEntity.getGuid()).isNotNull();
        Assertions.assertThat(recipeEntity.getSuitableFor()).isNotNull().isEqualTo(recipeRequest.getSuitableFor());
        Assertions.assertThat(recipeEntity.getName()).isNotNull().isEqualTo(recipeRequest.getName());
        Assertions.assertThat(recipeEntity.getCookingInstruction()).isNotNull().isEqualTo(recipeRequest.getCookingInstruction());
        Assertions.assertThat(recipeEntity.getVegetarian()).isEqualTo(recipeRequest.isVegetarian());
    }

    private RecipeRequest newRecipeRequest() {
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setName("Test");
        recipeRequest.setVegetarian(true);
        recipeRequest.setSuitableFor(2);
        recipeRequest.setIngredients(Set.of("Test Ingredient1", "Test Ingredient2", "Test Ingredient3"));
        recipeRequest.setCookingInstruction("Test Cooking Instruction");
        return recipeRequest;
    }

    private Recipe newRecipeEntity() {
        Recipe recipeEntity = new Recipe();
        recipeEntity.setId(1234L);
        recipeEntity.setName("Test");
        recipeEntity.setVegetarian(true);
        recipeEntity.setSuitableFor(2);
        List<Ingredient> ingredients = Stream.of("Test Ingredient1", "Test Ingredient2", "Test Ingredient3").map(Ingredient::new).collect(Collectors.toList());
        recipeEntity.setRecipeIngredient(new RecipeIngredient(ingredients));
        recipeEntity.setCookingInstruction("Test Cooking Instruction");
        return recipeEntity;
    }

    private User mockUserEntity() {
        User userEntity = new User();
        userEntity.setId(123L);
        userEntity.setEmailAddress("first.last@email.com");
        userEntity.setLastName("Last");
        userEntity.setFirstName("First");
        return userEntity;
    }
}
