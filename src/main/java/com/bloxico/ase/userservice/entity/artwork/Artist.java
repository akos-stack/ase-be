package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Entity
@Table(name = "artists")
public class Artist extends BaseEntity {

    @Column(name = "name")
    private String name;
}
