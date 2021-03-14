package com.bloxico.ase.userservice.entity.artwork;

import com.bloxico.ase.userservice.entity.BaseEntityAudit;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "artworks_documents")
public class ArtworkDocument extends BaseEntityAudit {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "artwork_id")
        Long artworkId;

        @Column(name = "document_id")
        Long documentId;

    }

    @EmbeddedId
    private Id id;

}
