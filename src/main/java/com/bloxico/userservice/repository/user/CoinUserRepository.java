package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.CoinUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinUserRepository extends JpaRepository<CoinUser, Long> {

    Optional<CoinUser> findByEmailIgnoreCase(String email);

    Optional<CoinUser> findById(long id);

    @EntityGraph(attributePaths = {"coinUserRoles.coinRole"})
    @Transactional
    Optional<CoinUser> findUserWithRolesByEmailIgnoreCase(String email);

    @Modifying
    @Query("delete FROM CoinUser u WHERE u.id IN ?1 and u.enabled = false")
    void deleteDisabledUsersById(List<Long> ids);
}
