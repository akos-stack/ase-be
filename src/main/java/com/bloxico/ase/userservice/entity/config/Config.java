package com.bloxico.ase.userservice.entity.config;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(of = {"type", "value"}, callSuper = false)
@Entity
@Table(name = "configs")
public class Config extends BaseEntity {

    public enum Type {
        QUOTATION_PACKAGE_MIN_EVALUATIONS
    }

    @Column(name = "type", unique = true)
    @Enumerated(STRING)
    private Type type;

    @Column(name = "value")
    private String value;

}
