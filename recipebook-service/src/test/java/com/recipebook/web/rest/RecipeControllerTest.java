package com.recipebook.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.values.PageResponse;
import com.recipebook.domain.values.RecipeRequest;
import com.recipebook.domain.values.RecipeResponse;
import com.recipebook.service.RecipeService;
import com.recipebook.service.RecipeUserDetailsService;
import com.recipebook.service.TokenProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author - AvanishKishorPandey
 */

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private RecipeUserDetailsService userDetailsService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        TokenProvider tokenProvider() {
            return new TokenProvider();
        }
    }

    @Test
    @WithMockUser
    void shouldReturnAllRecipes() throws Exception {
        Mockito.when(recipeService.findAll(0, 10)).thenReturn(mockPageResponse());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalItems", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPage", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].name", Matchers.is("Test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].createdAt", Matchers.is("13‐12‐2021 01:10")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].vegetarian", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].cookingInstruction", Matchers.is("Test Cooking Instruction")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].suitableFor", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].ingredients", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contents[0].ingredients", Matchers.hasSize(3)));
    }

    @Test
    void shouldReturn401WhenAllRecipesCalledWithoutLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void shouldCreateRecipe() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();
        Mockito.when(recipeService.createRecipe(Mockito.any(RecipeRequest.class))).thenReturn(mockRecipeResponse());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.uuid", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(recipeRequest.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suitableFor", Matchers.is(recipeRequest.getSuitableFor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cookingInstruction", Matchers.is(recipeRequest.getCookingInstruction())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vegetarian", Matchers.is(recipeRequest.isVegetarian())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients", Matchers.hasSize(recipeRequest.getIngredients().size())));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenRequiredValueIsMissingInRequest() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();
        recipeRequest.setName("");

        this.mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Validation error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].object", Matchers.is("recipeRequest")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].field", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].rejectedValue", Matchers.is("")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].message", Matchers.is("Recipe name is required")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", Matchers.is(400)));
    }

    @Test
    void shouldReturn401WhenCreateRecipesCalledWithoutLogin() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void shouldUpdateRecipe() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();
        RecipeResponse recipeResponse = mockRecipeResponse();
        Mockito.when(recipeService.updateRecipe(Mockito.any(RecipeRequest.class))).thenReturn(recipeResponse);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/recipes/{recipeGuid}", recipeResponse.getUuid())
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(recipeRequest.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suitableFor", Matchers.is(recipeRequest.getSuitableFor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cookingInstruction", Matchers.is(recipeRequest.getCookingInstruction())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vegetarian", Matchers.is(recipeRequest.isVegetarian())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients", Matchers.hasSize(recipeRequest.getIngredients().size())));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenRecipeIsNotExists() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();
        RecipeResponse recipeResponse = mockRecipeResponse();
        Mockito.when(recipeService.updateRecipe(Mockito.any(RecipeRequest.class))).thenThrow(new ResourceNotFoundException("Recipe", "GUID", recipeResponse.getUuid()));

        this.mockMvc.perform(MockMvcRequestBuilders.put("/recipes/{recipeGuid}", recipeResponse.getUuid())
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.NOT_FOUND.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is(String.format("Recipe not found with GUID : '%s'", recipeResponse.getUuid()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is(String.format("Recipe not found with GUID : '%s'", recipeResponse.getUuid()))));
    }

    @Test
    @WithMockUser
    void shouldReturn403WhenUserIsNotAllowedToPerformUpdate() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();
        RecipeResponse recipeResponse = mockRecipeResponse();
        Mockito.when(recipeService.updateRecipe(Mockito.any(RecipeRequest.class))).thenThrow(new AccessDeniedException("You don't have permission to edit/delete this record."));

        this.mockMvc.perform(MockMvcRequestBuilders.put("/recipes/{recipeGuid}", recipeResponse.getUuid())
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FORBIDDEN.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("You don't have permission to edit/delete this record.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is("You don't have permission to edit/delete this record.")));
    }

    @Test
    void shouldReturn401WhenUpdateRecipesCalledWithoutLogin() throws Exception {
        RecipeRequest recipeRequest = mockRecipeRequest();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/recipes/{recipeGuid}", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void shouldDeleteRecipe() throws Exception {
        final String recipeGuid = UUID.randomUUID().toString();
        Mockito.doNothing().when(recipeService).deleteRecipe(recipeGuid);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{recipeGuid}", recipeGuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenDeleteRecipeIsNotExists() throws Exception {
        final String recipeGuid = UUID.randomUUID().toString();
        Mockito.doThrow(new ResourceNotFoundException("Recipe", "GUID", recipeGuid)).when(recipeService).deleteRecipe(recipeGuid);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{recipeGuid}", recipeGuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.NOT_FOUND.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is(String.format("Recipe not found with GUID : '%s'", recipeGuid))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is(String.format("Recipe not found with GUID : '%s'", recipeGuid))));
    }

    @Test
    @WithMockUser
    void shouldReturn403WhenDeleteUserIsNotAllowedToPerformUpdate() throws Exception {
        final String recipeGuid = UUID.randomUUID().toString();
        Mockito.doThrow(new AccessDeniedException("You don't have permission to edit/delete this record.")).when(recipeService).deleteRecipe(recipeGuid);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{recipeGuid}", recipeGuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FORBIDDEN.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("You don't have permission to edit/delete this record.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is("You don't have permission to edit/delete this record.")));
    }

    @Test
    void shouldReturn401WhenDeleteRecipesCalledWithoutLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{recipeGuid}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }


    private PageResponse<RecipeResponse> mockPageResponse() {
        PageResponse<RecipeResponse> pageResponse = new PageResponse<>();
        pageResponse.setCurrentPage(0);
        pageResponse.setTotalPages(1);
        pageResponse.setTotalItems(1);
        pageResponse.setContents(List.of(mockRecipeResponse()));
        return pageResponse;
    }

    private RecipeResponse mockRecipeResponse() {
        return RecipeResponse.builder().name("Test").cookingInstruction("Test Cooking Instruction")
                .createdAt("13‐12‐2021 01:10").isVegetarian(true)
                .suitableFor(2).uuid(UUID.randomUUID().toString())
                .ingredients(Set.of("Test Ingredient1", "Test Ingredient2", "Test Ingredient3")).build();
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
