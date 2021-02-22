package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Entity
@Table(name = "artists")
public class Artist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
