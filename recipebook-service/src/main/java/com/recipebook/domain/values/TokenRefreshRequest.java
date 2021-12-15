package com.recipebook.domain.values;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author - AvanishKishorPandey
 */

@Data
public class TokenRefreshRequest implements Serializable {
	private static final long serialVersionUID = -7344277924032927714L;
	
	@NotBlank
    private String refreshToken;
}
