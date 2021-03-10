package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocument;
import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileDocumentRepository extends JpaRepository<UserProfileDocument, UserProfileDocumentId> {

    Optional<UserProfileDocument> findByUserProfileDocumentId_UserProfileId(long userId);

}
