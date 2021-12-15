package com.recipebook.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recipe")
@SQLDelete(sql = "UPDATE recipe SET deleted = true WHERE id = ? and version = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
@SequenceGenerator(name = Recipe.RECIPE_SEQUENCE_GENERATOR_NAME, sequenceName = Recipe.RECIPE_SEQUENCE_GENERATOR_NAME, allocationSize = 1)
public class Recipe extends AbstractBaseEntity {
    public static final String RECIPE_SEQUENCE_GENERATOR_NAME = "recipe_sequence";

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = RECIPE_SEQUENCE_GENERATOR_NAME)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "vegetarian", nullable = false)
    private Boolean vegetarian = Boolean.FALSE;

    @Column(name = "suitable_for")
    private Integer suitableFor;

    @Embedded
    private RecipeIngredient recipeIngredient;

    @Column(columnDefinition="TEXT", name = "instruction", nullable = false)
    private String cookingInstruction;

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;
}
