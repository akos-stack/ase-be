package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "artwork_groups")
public class ArtworkGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy="group")
    private Set<Artwork> artworks;
}
