package com.recipebook.domain.values;

import lombok.Builder;
import lombok.Data;

/**
 * @author - AvanishKishorPandey
 */

@Data
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
}
