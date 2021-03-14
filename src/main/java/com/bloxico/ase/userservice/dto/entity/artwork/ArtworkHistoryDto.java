package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArtworkHistoryDto extends BaseEntityAuditDto {

    @JsonProperty("artwork_id")
    private Long artworkId;

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
