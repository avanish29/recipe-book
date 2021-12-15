package com.recipebook.service;

import com.recipebook.domain.entities.UserToken;
import com.recipebook.domain.exceptions.ResourceNotFoundException;
import com.recipebook.domain.exceptions.TokenRefreshException;
import com.recipebook.domain.values.TokenRefreshRequest;
import com.recipebook.domain.values.UserPrincipal;
import com.recipebook.repository.UserRepository;
import com.recipebook.repository.UserTokenRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class RefreshTokenService {
	private final UserTokenRepository tokenRepository;
	private final UserRepository userRepository;

	@Value("${api.auth.jwt.refreshtokendurationinsec}")
	private Long refreshTokenDurationSec = 3600L;

	public RefreshTokenService(final UserTokenRepository tokenRepository, final UserRepository userRepository) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
	}

	public String createRefreshToken(@NonNull final Long userId) {
		return userRepository.findById(userId)
				.map(user -> {
					UserToken refreshToken = new UserToken();
					refreshToken.setUser(user);
					refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSec));
					refreshToken.setToken(UUID.randomUUID().toString());
					refreshToken = tokenRepository.save(refreshToken);
					return refreshToken.getToken();
				})
				.orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
	}
	
	public UserPrincipal refreshToken(@NonNull final TokenRefreshRequest token) {
		final String requestRefreshToken = token.getRefreshToken();
		return tokenRepository.findByToken(requestRefreshToken)
					.map(this::verifyExpiration)
					.map(UserToken::getUser)
					.map(user -> new UserPrincipal(user.getId(), user.getGuid(), user.getFirstName(), user.getLastName(), null))
					.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not valid!"));
	}

	UserToken verifyExpiration(final UserToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			tokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
		}
		return token;
	}
}
