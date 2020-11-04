package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.CoinUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<CoinUserRole, Long> {
    Optional<CoinUserRole> findByCoinUserId(long coinUserId);


    void deleteByIdIn(List<Long> ids);

    void deleteById(Long id);
}
