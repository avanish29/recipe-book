package com.recipebook.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Access(AccessType.FIELD)
public class RecipeIngredient implements Serializable {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_ingredient", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<Ingredient> ingredients;
}
