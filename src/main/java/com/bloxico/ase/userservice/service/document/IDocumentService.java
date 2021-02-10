package com.bloxico.ase.userservice.service.document;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentService {

    DocumentDto saveDocument(MultipartFile file, FileCategory type, long principalId);

    ByteArrayResource getDocumentById(Long id);
}
