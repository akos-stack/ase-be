package com.bloxico.ase.userservice.entity.artwork.metadata;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.function.Supplier;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@MappedSuperclass
public abstract class ArtworkMetadata extends BaseEntity {

    public enum Type {

        CATEGORY(Category::new),
        MATERIAL(Material::new),
        MEDIUM(Medium::new),
        STYLE(Style::new);

        private final Supplier<ArtworkMetadata> creatorFn;

        Type(Supplier<ArtworkMetadata> creatorFn) {
            this.creatorFn = creatorFn;
        }

        public <T extends ArtworkMetadata> T newInstance() {
            //noinspection unchecked
            return (T) creatorFn.get();
        }

    }

    public enum Status {

        PENDING,
        APPROVED;

    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(STRING)
    private Status status;

}
