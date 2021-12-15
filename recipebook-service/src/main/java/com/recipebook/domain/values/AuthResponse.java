package com.recipebook.domain.values;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author - AvanishKishorPandey
 */

@Data
@Builder(setterPrefix = "with")
public class AuthResponse {
    @ToString.Exclude
    private final String accessToken;

    @ToString.Exclude
    private final String refreshToken;

    @JsonProperty("userInfo")
    private UserResponse userResponse;
}
