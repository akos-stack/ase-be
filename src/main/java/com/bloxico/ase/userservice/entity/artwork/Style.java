package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@Entity
@Table(name = "styles")
public class Style extends BaseEntity implements IArtworkMetadataEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "status")
    @Enumerated(STRING)
    private ArtworkMetadataStatus status;
}
