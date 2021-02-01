package com.bloxico.ase.userservice.entity.user.profile;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "art_owners")
@Entity
public class ArtOwner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

}
