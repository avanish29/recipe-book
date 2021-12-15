package com.recipebook.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recipe_user", indexes = {
        @Index(name = "IDX_USERS_EMAILADDRESS", columnList = "emailAddress")
})
@Where(clause = "deleted = false")
@SequenceGenerator(name = User.USER_SEQUENCE_GENERATOR_NAME, sequenceName = User.USER_SEQUENCE_GENERATOR_NAME, allocationSize = 1)
public class User extends AbstractBaseEntity {
    public static final String USER_SEQUENCE_GENERATOR_NAME = "user_sequence";

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = USER_SEQUENCE_GENERATOR_NAME)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "Value must be a well-formed email address.")
    @Column(nullable = false)
    private String emailAddress;

    private String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recipe> recipes;
}
