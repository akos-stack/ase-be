package com.bloxico.ase.userservice.entity.user.profile;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "evaluators")
@Entity
public class Evaluator extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

}
