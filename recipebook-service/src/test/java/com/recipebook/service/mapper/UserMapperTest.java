package com.recipebook.service.mapper;

import com.recipebook.domain.entities.User;
import com.recipebook.domain.values.SignupRequest;
import com.recipebook.domain.values.UserResponse;
import com.recipebook.service.mappers.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author - AvanishKishorPandey
 */
class UserMapperTest {
    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testSignupRequestToEntity() {
        SignupRequest signupRequest = newSignupRequest();
        User resultEntity =  userMapper.toEntity(signupRequest, passwordEncoder);

        Assertions.assertThat(resultEntity).isNotNull();
        Assertions.assertThat(resultEntity.getId()).isNull();
        Assertions.assertThat(resultEntity.getGuid()).isNotNull();
        Assertions.assertThat(resultEntity.getId()).isNull();
        Assertions.assertThat(resultEntity.getFirstName()).isNotNull().isEqualTo(signupRequest.getFirstName());
        Assertions.assertThat(resultEntity.getLastName()).isNotNull().isEqualTo(signupRequest.getLastName());
        Assertions.assertThat(resultEntity.getEmailAddress()).isNotNull().isEqualTo(signupRequest.getEmail());
        Assertions.assertThat(resultEntity.getPasswordHash()).isNotNull();
    }

    @Test
    void testSignupRequestToEntityWhenNull() {
        User resultEntity = userMapper.toEntity(null, passwordEncoder);
        Assertions.assertThat(resultEntity).isNull();
    }

    @Test
    void testUserToResponse() {
        User userEntity = newUserEntity();
        UserResponse userResponse =  userMapper.toResponse(userEntity);

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse.getId()).isNotNull();
        Assertions.assertThat(userResponse.getFirstName()).isNotNull().isEqualTo(userEntity.getFirstName());
        Assertions.assertThat(userResponse.getLastName()).isNotNull().isEqualTo(userEntity.getLastName());
    }

    @Test
    void testUserToResponseWhenNull() {
        UserResponse userResponse = userMapper.toResponse(null);
        Assertions.assertThat(userResponse).isNull();
    }

    private SignupRequest newSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("first.last@email.com");
        signupRequest.setPassword("FirstLast123");
        signupRequest.setLastName("Last");
        signupRequest.setFirstName("First");
        return signupRequest;
    }

    private User newUserEntity() {
        User userEntity = new User();
        userEntity.setId(123L);
        userEntity.setEmailAddress("first.last@email.com");
        userEntity.setLastName("Last");
        userEntity.setFirstName("First");
        return userEntity;
    }
}
