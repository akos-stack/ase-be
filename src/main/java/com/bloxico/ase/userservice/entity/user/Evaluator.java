package com.bloxico.ase.userservice.entity.user;

import com.bloxico.ase.userservice.entity.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"userProfile", "verifier"})
@Entity
public class Evaluator extends Metadata {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private UserProfile userProfile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "verifier")
    private UserProfile verifier;

    private LocalDateTime verified;

}
