package com.bloxico.ase.userservice.dto.entity.artwork;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ArtworkHistoryDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("appraisal_history")
    private String appraisalHistory;

    @JsonProperty("location_history")
    private String locationHistory;

    @JsonProperty("runs_history")
    private String runsHistory;

    @JsonProperty("maintenance_history")
    private String maintenanceHistory;

    @JsonProperty("notes")
    private String notes;
}
