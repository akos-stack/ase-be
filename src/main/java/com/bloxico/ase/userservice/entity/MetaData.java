package com.bloxico.ase.userservice.entity;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Metadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long creator, updater;
    private LocalDateTime created, updated;

    @Version
    private long version;

    @PrePersist
    void prePersist() {
        setCreated(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate() {
        if (getUpdater() == null)
            throw new IllegalStateException("updater is null");
        setUpdated(LocalDateTime.now());
    }

}
