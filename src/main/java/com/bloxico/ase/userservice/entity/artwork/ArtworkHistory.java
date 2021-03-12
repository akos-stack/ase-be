package com.bloxico.ase.userservice.entity.artwork;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "artworks_history")
public class ArtworkHistory {

    @Id
    @Column(name = "id")
    private Long id;

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
