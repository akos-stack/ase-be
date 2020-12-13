package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM UserProfile u WHERE u.enabled = FALSE AND id IN ?1")
    List<UserProfile> findAllDisabledByIds(Collection<Long> ids);

}
