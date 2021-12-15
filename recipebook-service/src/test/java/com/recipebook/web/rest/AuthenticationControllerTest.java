package com.recipebook.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipebook.domain.exceptions.ResourceAlreadyExistsException;
import com.recipebook.domain.values.*;
import com.recipebook.service.RecipeUserDetailsService;
import com.recipebook.service.RefreshTokenService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

/**
 * @author - AvanishKishorPandey
 */

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthenticationManager authenticationManager;

    @MockBean
    protected RefreshTokenService refreshTokenService;

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
    void shouldAuthenticateUser() throws Exception {
        final String refreshToken = UUID.randomUUID().toString();
        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class))).thenReturn(mockUsernamePasswordAuthenticationToken());
        Mockito.when(refreshTokenService.createRefreshToken(20L)).thenReturn(refreshToken);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .content(objectMapper.writeValueAsString(buildAuthRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", Matchers.is(refreshToken)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userInfo", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userInfo.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userInfo.firstName", Matchers.is("Avanish")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userInfo.lastName", Matchers.is("Pandey")));

    }

    @Test
    void shouldReturn403WhenUseWrongUserNameUsedForAuthentication() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class))).thenThrow(new UsernameNotFoundException("User not found with email : junt@test.com"));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .content(objectMapper.writeValueAsString(buildAuthRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FORBIDDEN.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("User not found with email : junt@test.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is("User not found with email : junt@test.com")));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        TokenRefreshRequest refreshRequest = buildTokenRefreshRequest();
        Mockito.when(refreshTokenService.refreshToken(Mockito.any(TokenRefreshRequest.class))).thenReturn(mockUserPrincipal());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
                        .content(objectMapper.writeValueAsString(refreshRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", Matchers.notNullValue()));

    }

    @Test
    void shouldReturn400ErrorWhenBodyIsMissingForRefreshToken() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    void shouldReturn400ErrorWhenRequestIsNotValidForRefreshToken() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
                        .content(objectMapper.writeValueAsString(new TokenRefreshRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    void shouldRegisterUser() throws Exception {
        SignupRequest signupRequest = mockSignupRequest();
        Mockito.when(userDetailsService.registerUser(Mockito.any(SignupRequest.class))).thenReturn(mockUserResponse());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .content(objectMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(signupRequest.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(signupRequest.getLastName())));

    }

    @Test
    void shouldReturn409WhenUserAlreadyPresent() throws Exception {
        SignupRequest signupRequest = mockSignupRequest();
        Mockito.when(userDetailsService.registerUser(Mockito.any(SignupRequest.class))).thenThrow(new ResourceAlreadyExistsException("User already exists by email 'first.last@email.com'."));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .content(objectMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.CONFLICT.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("User already exists by email 'first.last@email.com'.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage", Matchers.is("User already exists by email 'first.last@email.com'.")));

    }

    @Test
    void shouldReturn400WhenRequestIsNotValid() throws Exception {
        SignupRequest signupRequest = mockSignupRequest();
        signupRequest.setEmail("abc");

        this.mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .content(objectMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Validation error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].object", Matchers.is("signupRequest")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].field", Matchers.is("email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].rejectedValue", Matchers.is("abc")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors[0].message", Matchers.is("Email is invalid")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode", Matchers.is(400)));
    }

    private TokenRefreshRequest buildTokenRefreshRequest() {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(UUID.randomUUID().toString());
        return refreshRequest;
    }

    private AuthRequest buildAuthRequest() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("junt@test.com");
        authRequest.setPassword("junittest");
        return authRequest;
    }

    private UsernamePasswordAuthenticationToken mockUsernamePasswordAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(mockUserPrincipal(), null);
    }

    private UserPrincipal mockUserPrincipal() {
        return new UserPrincipal(20L, UUID.randomUUID().toString(), "Avanish", "Pandey", null);
    }

    private SignupRequest mockSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("First");
        signupRequest.setLastName("Last");
        signupRequest.setEmail("first.last@email.com");
        signupRequest.setPassword("Test123");
        return signupRequest;
    }

    private UserResponse mockUserResponse() {
        return UserResponse.builder().id(UUID.randomUUID().toString())
                .firstName("First").lastName("Last").build();
    }
}
