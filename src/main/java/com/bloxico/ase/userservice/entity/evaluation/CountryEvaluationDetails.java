package com.bloxico.ase.userservice.entity.evaluation;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of = "countryId", callSuper = false)
@Entity
@Table(name = "country_evaluation_details")
public class CountryEvaluationDetails extends BaseEntity {

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "price_per_evaluation")
    private Integer pricePerEvaluation;

    @Column(name = "availability_percentage")
    private Integer availabilityPercentage;

}
