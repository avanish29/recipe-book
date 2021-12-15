package com.recipebook.domain.values;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author - AvanishKishorPandey
 */

@Data
@Builder
public class RecipeResponse implements Serializable {
    private String uuid;
    private String createdAt;
    private String name;
    private boolean isVegetarian;
    private int suitableFor;
    private Set<String> ingredients;
    private String cookingInstruction;
}
