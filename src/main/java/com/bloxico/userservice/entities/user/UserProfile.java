package com.bloxico.userservice.entities.user;


import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = {"name", "city", "region"}, callSuper = false)
@ToString(exclude = "coinUser")
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(nullable = false, name = "region_id")
    private Region region;

    @OneToOne
    @JoinColumn(nullable = false, name = "coin_user_id")
    private CoinUser coinUser;

}
