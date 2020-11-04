package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "userProfile")
@Table(name = "owners")
@Entity
public class Owner extends BaseEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private UserProfile userProfile;

    @Column(name = "birthday")
    private LocalDate birthday;

}
