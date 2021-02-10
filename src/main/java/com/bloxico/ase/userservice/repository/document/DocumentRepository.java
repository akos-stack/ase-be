package com.bloxico.ase.userservice.repository.document;

import com.bloxico.ase.userservice.entity.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
