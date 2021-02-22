package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "artwork_groups")
public class ArtworkGroup extends BaseEntity {

    public enum Status {

        DRAFT,
        WAITING_FOR_EVALUATION

    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    private Status status;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<Artwork> artworks;

}
