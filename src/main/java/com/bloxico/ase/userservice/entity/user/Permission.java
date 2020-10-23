package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.MetaData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Entity
public class Permission extends MetaData {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

    @NaturalId
    private String name;

}
