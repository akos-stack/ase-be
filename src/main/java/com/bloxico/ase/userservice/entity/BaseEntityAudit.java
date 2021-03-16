package com.bloxico.ase.userservice.entity;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Version;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "creator_id")
    private Long creatorId;

    @LastModifiedBy
    @Column(name = "updater_id")
    private Long updaterId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private long version;

}
