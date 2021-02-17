package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.address.Location;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "artworks")
public class Artwork implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private ArtOwner owner;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "weight", nullable = false)
    private BigDecimal weight;

    @Column(name = "height", nullable = false)
    private BigDecimal height;

    @Column(name = "width", nullable = false)
    private BigDecimal width;

    @Column(name = "depth", nullable = false)
    private BigDecimal depth;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    private ArtworkStatus status;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ArtworkGroup group;

    @OneToOne(mappedBy = "artwork")
    ArtworkHistory artworkHistory;

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

    @ManyToMany(fetch = LAZY, cascade = MERGE)
    @JoinTable(
            name = "artworks_documents",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private List<Document> documents;
}
