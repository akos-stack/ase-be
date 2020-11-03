package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.userservice.entity.user.UserProfile;
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
    private Long creator;

    @Column(name = "updater_id")
    private Long updater;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    @Version
    private long version;

    @PrePersist
    void prePersist() {
        if (!(this instanceof UserProfile))
            requireNonNull(getCreator());
        setCreated(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate() {
        requireNonNull(getUpdater());
        setUpdated(LocalDateTime.now());
    }

}
