package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "userProfile")
@Entity
public class Owner extends BaseEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private UserProfile userProfile;

    private LocalDate birthday;

}
