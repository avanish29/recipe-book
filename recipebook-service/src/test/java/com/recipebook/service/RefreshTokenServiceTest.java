package com.recipebook.service;

import com.recipebook.domain.entities.User;
import com.recipebook.domain.entities.UserToken;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.exceptions.TokenRefreshException;
import com.recipebook.domain.values.TokenRefreshRequest;
import com.recipebook.domain.values.UserPrincipal;
import com.recipebook.repository.UserRepository;
import com.recipebook.repository.UserTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author - AvanishKishorPandey
 */

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock
    private UserTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setup() {
        refreshTokenService = new RefreshTokenService(tokenRepository, userRepository);
    }

    @Test
    void testCreateRefreshToken() {
        Long userId = 20L;
        UserToken mockUserToken = mockUserToken(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserToken.getUser()));
        Mockito.when(tokenRepository.save(Mockito.any(UserToken.class))).thenReturn(mockUserToken);

        String refreshToken = refreshTokenService.createRefreshToken(userId);
        Assertions.assertThat(refreshToken).isNotNull().isEqualTo(mockUserToken.getToken());
    }

    @Test
    void testCreateRefreshTokenForUnkownUser() {
        Long userId = 20L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.createRefreshToken(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with ID : '20'");
    }

    @Test
    void testRefreshToken() {
        Long userId = 20L;
        UserToken mockUserToken = mockUserToken(userId);

        Mockito.when(tokenRepository.findByToken(mockUserToken.getToken())).thenReturn(Optional.of(mockUserToken));

        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken(mockUserToken.getToken());
        UserPrincipal userPrincipal = refreshTokenService.refreshToken(tokenRefreshRequest);

        Assertions.assertThat(userPrincipal).isNotNull();
        Assertions.assertThat(userPrincipal.getUsername()).isEqualTo(mockUserToken.getUser().getGuid());
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        Mockito.when(tokenRepository.findByToken(Mockito.anyString())).thenReturn(Optional.empty());

        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken(UUID.randomUUID().toString());
        assertThatThrownBy(() -> refreshTokenService.refreshToken(tokenRefreshRequest))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageEndingWith("Refresh token is not valid!");
    }

    @Test
    void testRefreshTokenWithInvalidExpire() {
        Long userId = 20L;
        UserToken mockUserToken = mockUserToken(userId);
        mockUserToken.setExpiryDate(Instant.now().minus(40, ChronoUnit.DAYS));

        Mockito.when(tokenRepository.findByToken(mockUserToken.getToken())).thenReturn(Optional.of(mockUserToken));
        Mockito.doNothing().when(tokenRepository).delete(mockUserToken);

        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken(mockUserToken.getToken());
        assertThatThrownBy(() -> refreshTokenService.refreshToken(tokenRefreshRequest))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageEndingWith("Refresh token was expired. Please make a new signin request");
    }

    private UserToken mockUserToken(final Long userId) {
        UserToken refreshToken = new UserToken();
        refreshToken.setUser(mockUser(userId));
        refreshToken.setExpiryDate(Instant.now().plusMillis(60*60*60));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshToken;
    }

    private User mockUser(final Long userId) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setDeleted(Boolean.FALSE);
        newUser.setGuid(UUID.randomUUID().toString());
        newUser.setCreatedOn(LocalDateTime.now());
        newUser.setFirstName("Junit");
        newUser.setLastName("Test");
        newUser.setEmailAddress("junit.test@gmail.com");
        return newUser;
    }
}
