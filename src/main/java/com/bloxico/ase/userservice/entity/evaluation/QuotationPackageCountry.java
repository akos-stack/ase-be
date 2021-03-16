package com.bloxico.ase.userservice.entity.evaluation;

import com.bloxico.ase.userservice.entity.BaseEntityAudit;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "quotation_packages_countries")
public class QuotationPackageCountry extends BaseEntityAudit {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "quotation_package_id")
        Long quotationPackageId;

        @Column(name = "country_id")
        Long countryId;

    }

    @EmbeddedId
    private Id id;

    @Column(name = "number_of_evaluations")
    private Integer numberOfEvaluations;

}
