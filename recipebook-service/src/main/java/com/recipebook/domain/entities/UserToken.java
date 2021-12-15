package com.recipebook.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_token", indexes = {
		@Index(name = "IDX_USERTOKEN_TOKEN", columnList = "token", unique = true)
})
public class UserToken {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Instant expiryDate;
}
