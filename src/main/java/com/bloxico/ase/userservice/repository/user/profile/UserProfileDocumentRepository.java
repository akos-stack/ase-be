package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocument;
import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileDocumentRepository extends JpaRepository<UserProfileDocument, UserProfileDocumentId> {
}
