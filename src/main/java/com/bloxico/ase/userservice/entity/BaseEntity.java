package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.userservice.entity.user.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "updater_id")
    private Long updaterId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private long version;

    @PrePersist
    void prePersist() {
        if (!(this instanceof User))
            requireNonNull(getCreatorId());
        setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate() {
        requireNonNull(getUpdaterId());
        setUpdatedAt(LocalDateTime.now());
    }

}
