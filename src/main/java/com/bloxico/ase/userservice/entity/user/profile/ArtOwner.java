package com.bloxico.ase.userservice.entity.user.profile;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "art_owners")
@Entity
public class ArtOwner extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

}
