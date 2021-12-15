package com.recipebook.service.mappers;

import com.recipebook.domain.entities.User;
import com.recipebook.domain.values.SignupRequest;
import com.recipebook.domain.values.UserResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author - AvanishKishorPandey
 */

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "emailAddress", source = "email")
    User toEntity(SignupRequest signupRequest, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "id", source = "guid")
    UserResponse toResponse(final User user);

    @AfterMapping
    default void mapPassword(@MappingTarget User userEntity, SignupRequest signupRequest, @Context PasswordEncoder passwordEncoder) {
        userEntity.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));
    }
}
