package com.bloxico.ase.userservice.service.document;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentService {

    DocumentDto saveDocument(MultipartFile file, FileCategory type);

    DocumentDto saveDocument(MultipartFile file, FileCategory type, Long principalId);

    ByteArrayResource getDocumentById(Long id);

    DocumentDto updateDocumentType(Long id, FileCategory type);

    List<DocumentDto> getDocumentsById(List<Long> ids);
}
