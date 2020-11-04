package com.bloxico.ase.userservice.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Table(name = "permissions")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

    @Column(name = "name")
    private String name;

}