package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ArtworkMetadataRepository<Category> {
}
