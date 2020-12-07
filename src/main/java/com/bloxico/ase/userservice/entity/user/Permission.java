package com.bloxico.ase.userservice.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name")
@Table(name = "permissions")
@Entity
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

    @Column(name = "name")
    private String name;

}
