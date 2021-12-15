package com.recipebook.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Access(AccessType.FIELD)
public class Ingredient implements Serializable {
    private String name;
}
