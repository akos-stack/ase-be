package com.bloxico.ase.userservice.entity.user.profile;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "userProfile", callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "art_owners")
@Entity
public class ArtOwner extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

}
