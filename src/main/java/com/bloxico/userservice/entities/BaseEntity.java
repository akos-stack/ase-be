package com.bloxico.userservice.entities;

import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@ToString(of = {"id"})
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    protected Long id;

    @Column(name = "version")
    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    /**
     * Audit
     */
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    @PrePersist
    public void setCreationDate() {
        this.created = new Date();
    }

    @PreUpdate
    public void setChangeDate() {
        this.updated = new Date();
    }
}