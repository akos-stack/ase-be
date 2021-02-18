package com.bloxico.ase.userservice.entity.artwork;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "artworks_history")
public class ArtworkHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private Artwork artwork;

    @Column(name = "appraisal_history")
    private String appraisalHistory;

    @Column(name = "location_history")
    private String locationHistory;

    @Column(name = "runs_history")
    private String runsHistory;

    @Column(name = "maintenance_history")
    private String maintenanceHistory;

    @Column(name = "notes")
    private String notes;
}
