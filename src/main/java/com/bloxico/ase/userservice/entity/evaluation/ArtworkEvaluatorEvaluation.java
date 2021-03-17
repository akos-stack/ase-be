package com.bloxico.ase.userservice.entity.evaluation;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"artworkId", "evaluatorId"}, callSuper = false)
@Entity
@Table(name = "artwork_evaluator_evaluations")
public class ArtworkEvaluatorEvaluation extends BaseEntity {

    @Column(name = "artwork_id")
    private Long artworkId;

    @Column(name = "evaluator_id")
    private Long evaluatorId;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @Column(name = "ase_sellable")
    private Boolean aseSellable;

    @Column(name = "send_offer")
    private Boolean sendOffer;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment")
    private String comment;

}
