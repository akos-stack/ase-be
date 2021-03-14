package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntityAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "artwork_histories")
public class ArtworkHistory extends BaseEntityAudit {

    @Id
    @Column(name = "artwork_id")
    private Long artworkId;

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
