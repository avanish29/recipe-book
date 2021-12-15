package com.recipebook.service;

import com.recipebook.domain.entities.User;
import com.recipebook.domain.exceptions.ResourceAlreadyExistsException;
import com.recipebook.domain.values.SignupRequest;
import com.recipebook.domain.values.UserResponse;
import com.recipebook.repository.RecipeRepository;
import com.recipebook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.support.NoOpCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author - AvanishKishorPandey
 */

@ExtendWith(MockitoExtension.class)
class RecipeUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    private RecipeUserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setup() {
        userDetailsService = new RecipeUserDetailsService(userRepository, passwordEncoder);
    }

    @Test
    void testLoadUserByUsername() {
        User mockUser = buildUser();
        Mockito.when(userRepository.findByEmailAddressIgnoreCase("junit.test@gmail.com")).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("junit.test@gmail.com");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(mockUser.getGuid());
    }

    @Test
    void testLoadUserByUsernameWhenUserNotPresent() {
        Mockito.when(userRepository.findByEmailAddressIgnoreCase("junit.test@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("junit.test@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found by email : junit.test@gmail.com");
    }

    @Test
    void testRegisterUser() {
        final SignupRequest signupRequest = this.mockSignupRequest();
        final User userEntity = buildUser();

        Mockito.when(userRepository.existsByEmailAddressIgnoreCase(signupRequest.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userEntity);

        UserResponse userResponse = userDetailsService.registerUser(signupRequest);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(userEntity.getGuid());
    }

    @Test
    void testRegisterUserWhenUserAlreadyExists() {
        final SignupRequest signupRequest = this.mockSignupRequest();

        Mockito.when(userRepository.existsByEmailAddressIgnoreCase(signupRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userDetailsService.registerUser(signupRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(String.format("User already exists by email '%s'.", signupRequest.getEmail()));
    }

    private User buildUser() {
        User newUser = new User();
        newUser.setId(23L);
        newUser.setDeleted(Boolean.FALSE);
        newUser.setGuid(UUID.randomUUID().toString());
        newUser.setCreatedOn(LocalDateTime.now());
        newUser.setFirstName("Junit");
        newUser.setLastName("Test");
        newUser.setEmailAddress("junit.test@gmail.com");
        newUser.setPasswordHash(passwordEncoder.encode("AbcD@123"));
        return newUser;
    }

    private SignupRequest mockSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("First");
        signupRequest.setLastName("Last");
        signupRequest.setEmail("first.last@email.com");
        signupRequest.setPassword("Test123");
        return signupRequest;
    }
}
