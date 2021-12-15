package com.recipebook.domain.values;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * @author - AvanishKishorPandey
 */

@Data
public class RecipeRequest implements Serializable {
    @JsonIgnore
    private String guid;

    @NotBlank(message = "Recipe name is required")
    private String name;

    private boolean isVegetarian;

    @Min(value = 1, message = "Recipe should be suitable for minimum 1 person")
    private int suitableFor;

    @NotNull(message = "Recipe ingredient is required")
    @Size(min = 1, message = "At least one ingredient is required.")
    private Set<String> ingredients;

    @NotBlank(message = "Recipe cooking instruction is required")
    private String cookingInstruction;
}
