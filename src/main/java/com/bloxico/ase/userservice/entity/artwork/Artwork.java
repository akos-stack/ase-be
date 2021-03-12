package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.artwork.metadata.Category;
import com.bloxico.ase.userservice.entity.artwork.metadata.Material;
import com.bloxico.ase.userservice.entity.artwork.metadata.Medium;
import com.bloxico.ase.userservice.entity.artwork.metadata.Style;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "artworks")
public class Artwork extends BaseEntity {

    public enum Status {

        DRAFT,
        READY_FOR_EVALUATION,
        WAITING_FOR_EVALUATION

    }

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "height")
    private BigDecimal height;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "depth")
    private BigDecimal depth;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    private Status status;

    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "artworks_categories",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "artworks_materials",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    private List<Material> materials;

    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "artworks_mediums",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "medium_id"))
    private List<Medium> mediums;

    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "artworks_styles",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id"))
    private List<Style> styles;
}
