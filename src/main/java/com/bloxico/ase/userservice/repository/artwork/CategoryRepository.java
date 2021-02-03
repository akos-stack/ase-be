package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.entity.artwork.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    @Query(value = "SELECT c FROM Category c WHERE (:status IS NULL or c.status = :status) and (:name IS NULL or c.name like %:name%)")
    Page<Category> fetchCategories(@Param("status") ArtworkMetadataStatus status, @Param("name") String name, Pageable pageable);

    List<Category> findAllByStatusAndNameContains(ArtworkMetadataStatus status, String name);

}
