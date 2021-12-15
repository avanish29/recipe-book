package com.recipebook.domain.values;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author - AvanishKishorPandey
 */

@Data
public class AuthRequest {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    @ToString.Exclude
    private String password;
}
