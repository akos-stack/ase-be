package com.bloxico.ase.userservice.entity.config;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(of = {"type", "value"}, callSuper = false)
@Entity
@Table(name = "configs")
public class Config extends BaseEntity {

    public enum Type {

        QUOTATION_PACKAGE_MIN_EVALUATIONS {
            @Override
            public boolean isValid(Object value) {
                return !Integer.class.isAssignableFrom(value.getClass())
                        && (int) value > 0;
            }
        };

        public abstract boolean isValid(Object value);

    }

    @Column(name = "type", unique = true)
    @Enumerated(STRING)
    private Type type;

    @Column(name = "value")
    private String value;

}
