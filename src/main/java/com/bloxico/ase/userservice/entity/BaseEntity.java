package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.userservice.entity.user.UserProfile;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long creator, updater;
    private LocalDateTime created, updated;

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
