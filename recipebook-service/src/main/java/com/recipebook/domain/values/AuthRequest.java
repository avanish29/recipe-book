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
    @NotBlank(message = "Username is required")
    @Email
    private String username;

    @NotBlank(message = "Password is required")
    @ToString.Exclude
    private String password;
}
