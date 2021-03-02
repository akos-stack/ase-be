package com.bloxico.ase.userservice.entity.artwork.metadata;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.function.Supplier;

import static javax.persistence.EnumType.STRING;

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

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(STRING)
    private Status status;

}
