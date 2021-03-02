package com.bloxico.ase.userservice.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@MappedSuperclass
public abstract class BaseEntity extends BaseEntityAudit {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
}
