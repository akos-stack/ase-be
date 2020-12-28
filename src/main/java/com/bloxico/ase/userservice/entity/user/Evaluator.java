package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "evaluators")
@Entity
public class Evaluator extends BaseEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private UserProfile userProfile;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

}
