package com.recipebook.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author - AvanishKishorPandey
 */

@Getter
@Setter
@MappedSuperclass
abstract class AbstractBaseEntity implements Serializable {
    protected abstract Long getId();

    @Column(nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdOn = LocalDateTime.now();

    @Column(nullable = false)
    protected Boolean deleted = Boolean.FALSE;

    @Column(unique=true, nullable=false, updatable=false, length = 36)
    protected String guid = UUID.randomUUID().toString();

    @Column(nullable = false)
    @Version
    protected int version;
}
