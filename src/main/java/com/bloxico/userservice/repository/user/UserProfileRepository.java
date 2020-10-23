package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userProfileRepositoryOld")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("select up FROM userProfileOld up WHERE up.coinUser.email = ?1")
    Optional<UserProfile> findByEmail(String email);
}
