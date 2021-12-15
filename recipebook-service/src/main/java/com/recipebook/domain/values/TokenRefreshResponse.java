package com.recipebook.domain.values;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author - AvanishKishorPandey
 */

@Data
@Builder(setterPrefix = "with")
public class TokenRefreshResponse implements Serializable {
    @ToString.Exclude
    private final String accessToken;
}
