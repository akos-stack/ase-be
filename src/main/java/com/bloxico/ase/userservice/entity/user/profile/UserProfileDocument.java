package com.bloxico.ase.userservice.entity.user.profile;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_profiles_documents")
public class UserProfileDocument implements Serializable {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "user_profile_id")
        private Long userProfileId;

        @Column(name = "document_id")
        private Long documentId;

    }

    @EmbeddedId
    private Id id;

}
