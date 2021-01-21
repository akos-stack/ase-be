package com.bloxico.ase.userservice.entity.aspiration;

import com.bloxico.ase.userservice.entity.user.Role;
import lombok.Data;

import javax.persistence.*;

import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "aspirations")
public class Aspiration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Short id;

    @OneToOne
    @JoinColumn(name="role_id")
    private Role role;

}
