package com.recipebook.service;

import com.recipebook.domain.entities.User;
import com.recipebook.domain.exceptions.ResourceAlreadyExistsException;
import com.recipebook.domain.values.SignupRequest;
import com.recipebook.domain.values.UserPrincipal;
import com.recipebook.domain.values.UserResponse;
import com.recipebook.repository.UserRepository;
import com.recipebook.service.mappers.UserMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author - AvanishKishorPandey
 */

@Service
@Transactional
@Slf4j
public class RecipeUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RecipeUserDetailsService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Locates the user based on the email.
     * @param email - The email identifying the user whose data is required.
     * @return - a fully populated user record.
     * @throws UsernameNotFoundException - if the user could not be found or the user has noGrantedAuthority.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmailAddressIgnoreCase(email)
                .map(user -> new UserPrincipal(user.getId(), user.getGuid(), user.getFirstName(), user.getLastName(), user.getPasswordHash()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email : " + email));
    }

    public UserResponse registerUser(@NonNull final SignupRequest signupRequest) {
        log.debug("Registering new user with details :: {}", signupRequest);
        return Optional.of(signupRequest)
                .filter(request -> !this.userRepository.existsByEmailAddressIgnoreCase(request.getEmail()))
                .map(request -> {
                    User newUser = UserMapper.INSTANCE.toEntity(request, passwordEncoder);
                    newUser = this.userRepository.save(newUser);
                    log.debug("New user created successfully with Id :: {}", newUser.getId());
                    return UserMapper.INSTANCE.toResponse(newUser);
                })
                .orElseThrow(() -> new ResourceAlreadyExistsException(String.format("User already exists by email '%s'.", signupRequest.getEmail())));
    }
}
