package com.recipebook;

import com.recipebook.domain.values.AuthResponse;
import com.recipebook.domain.values.PageResponse;
import com.recipebook.domain.values.RecipeResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipebookServiceApplicationTests {
	private static String accessToken;
	private static String recipeGuid;

	@Autowired
	private WebTestClient webClient;

	@BeforeEach
	public void setUp() {
		webClient = webClient
				.mutate()
				.responseTimeout(Duration.ofSeconds(5))
				.build();
	}

	@Test()
	@Order(1)
	void contextLoads(ApplicationContext context) {
		Assertions.assertThat(context).isNotNull();
	}

	@Test
	@Order(2)
	void givenNoUserName_whenSignin_thenGet400Error() {
		webClient.post().uri("/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"username\":\"\",\"password\":\"Test@123\"}")
				.exchange()
				.expectStatus()
				.isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.name())
				.jsonPath("$.message").isEqualTo("Validation error")
				.jsonPath("$.validationErrors").isNotEmpty()
				.jsonPath("$.validationErrors[0].field").isEqualTo("username")
				.jsonPath("$.validationErrors[0].message").isEqualTo("Username is required");
	}

	@Test
	@Order(3)
	void givenNoPassword_whenSignin_thenGet400Error() {
		webClient.post().uri("/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"username\":\"avanish.k.pandey@gmail.com\",\"password\":\"\"}")
				.exchange()
				.expectStatus()
				.isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.name())
				.jsonPath("$.message").isEqualTo("Validation error")
				.jsonPath("$.validationErrors").isNotEmpty()
				.jsonPath("$.validationErrors[0].field").isEqualTo("password")
				.jsonPath("$.validationErrors[0].message").isEqualTo("Password is required");
	}

	@Test
	@Order(4)
	void givenSinupRequest_whenSingup_thenGet200Success() {
		webClient.post().uri("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"firstName\":\"Avanish\",\"lastName\":\"Pandey\",\"email\":\"avanish.k.pandey@gmail.com\",\"password\":\"Test@123\"}")
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.firstName").isEqualTo("Avanish")
				.jsonPath("$.lastName").isEqualTo("Pandey");
	}

	@Test
	@Order(5)
	void givenSininRequest_whenSingin_thenGet200Success() {
		webClient.post().uri("/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"username\":\"avanish.k.pandey@gmail.com\",\"password\":\"Test@123\"}")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.accessToken").isNotEmpty()
				.jsonPath("$.refreshToken").isNotEmpty()
				.jsonPath("$.userInfo").isNotEmpty()
				.jsonPath("$.userInfo.id").isNotEmpty()
				.jsonPath("$.userInfo.firstName").isEqualTo("Avanish")
				.jsonPath("$.userInfo.lastName").isEqualTo("Pandey");
	}

	@Test
	@Order(6)
	void loginAndGetAccessToken() {
		accessToken = Objects.requireNonNull(webClient
						.post()
						.uri("/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.bodyValue("{\"username\":\"avanish.k.pandey@gmail.com\",\"password\":\"Test@123\"}")
						.exchange()
						.expectBody(AuthResponse.class)
						.returnResult()
						.getResponseBody())
				.getAccessToken();
	}

	@Test
	@Order(7)
	void givenCreateRecipeRequest_whenRecipes_thenGet200Success() {
		webClient.post().uri("/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.bodyValue("{\"name\":\"Pizza\",\"isVegetarian\":false,\"suitableFor\":2,\"ingredients\":[\"Pizza Base\",\"Tomatoes\"],\"cookingInstruction\":\"Put it in oven\"}")
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody()
				.jsonPath("$.uuid").isNotEmpty()
				.jsonPath("$.createdAt").isNotEmpty()
				.jsonPath("$.name").isEqualTo("Pizza")
				.jsonPath("$.suitableFor").isEqualTo(2)
				.jsonPath("$.vegetarian").isEqualTo(false)
				.jsonPath("$.ingredients").isNotEmpty()
				.jsonPath("$.ingredients").isArray()
				.jsonPath("$.cookingInstruction").isEqualTo("Put it in oven");
	}

	@Test
	@Order(8)
	void givenPageRequest_whenGETRecipes_thenGet200Success() {
		PageResponse recipes = webClient.get().uri("/recipes")
				.header("Authorization", "Bearer " + accessToken)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PageResponse.class)
				.returnResult()
				.getResponseBody();

		Assertions.assertThat(recipes).isNotNull();
		recipeGuid = (String) ((LinkedHashMap)recipes.getContents().get(0)).get("uuid");
	}

	@Test
	@Order(9)
	void givenUpdateRecipeRequest_whenRecipes_thenGet200Success() {
		webClient.put().uri("/recipes/{recipeGuid}", recipeGuid)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.bodyValue("{\"name\":\"Pizza Updated\",\"isVegetarian\":false,\"suitableFor\":2,\"ingredients\":[\"Pizza Base Updated\",\"Tomatoes Updated\"],\"cookingInstruction\":\"Put it in oven Updated\"}")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.uuid").isEqualTo(recipeGuid)
				.jsonPath("$.name").isEqualTo("Pizza Updated")
				.jsonPath("$.suitableFor").isEqualTo(2)
				.jsonPath("$.vegetarian").isEqualTo(false)
				.jsonPath("$.ingredients").isNotEmpty()
				.jsonPath("$.ingredients").isArray()
				.jsonPath("$.cookingInstruction").isEqualTo("Put it in oven Updated");
	}

	@Test
	@Order(10)
	void givenUpdateRecipeRequestWithoutAccessToken_whenRecipes_thenGet401Response() {
		webClient.put().uri("/recipes/{recipeGuid}", recipeGuid)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"name\":\"Pizza Updated\",\"isVegetarian\":false,\"suitableFor\":2,\"ingredients\":[\"Pizza Base Updated\",\"Tomatoes Updated\"],\"cookingInstruction\":\"Put it in oven Updated\"}")
				.exchange()
				.expectStatus()
				.isUnauthorized();
	}

	@Test
	@Order(11)
	void givenDeleteRecipeRequest_whenRecipes_thenGet200Success() {
		webClient.delete().uri("/recipes/{recipeGuid}", recipeGuid)
				.header("Authorization", "Bearer " + accessToken)
				.exchange()
				.expectStatus()
				.isNoContent();
	}

	@Test
	@Order(12)
	void givenDeleteRecipeRequestWithoutAccessToken_whenRecipes_thenGet401Response() {
		webClient.delete().uri("/recipes/{recipeGuid}", recipeGuid)
				.exchange()
				.expectStatus()
				.isUnauthorized();
	}
}
