package com.bloxico.ase.userservice.service.document;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentService {

    DocumentDto saveDocument(MultipartFile file, FileCategory type);

    DocumentDto saveDocument(MultipartFile file, FileCategory type, long principalId);

    ByteArrayResource findDocumentById(long id);

    DocumentDto updateDocumentType(long id, FileCategory type);

    List<DocumentDto> findDocumentsByIds(List<Long> ids);

    List<DocumentDto> findDocumentsByArtworkId(long id);

    void deleteDocumentById(long id);

    void deleteDocumentsByIds(List<Long> id);

}
