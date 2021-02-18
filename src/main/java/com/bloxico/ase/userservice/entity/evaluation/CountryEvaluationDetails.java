package com.bloxico.ase.userservice.entity.evaluation;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "countryId", callSuper = false)
@Entity
@Table(name = "country_evaluation_details")
public class CountryEvaluationDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "price_per_evaluation")
    private Integer pricePerEvaluation;

    @Column(name = "availability_percentage")
    private Integer availabilityPercentage;

}
