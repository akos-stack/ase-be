package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtOwnerRepository extends JpaRepository<ArtOwner, Long> {

    Optional<ArtOwner> findByUserProfile_UserId(Long id);

}
