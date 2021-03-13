package com.bloxico.ase.userservice.service.document.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilDocument;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.ase.userservice.exception.DocumentException;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DocumentServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilDocument utilDocument;
    @Autowired private DocumentServiceImpl documentService;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private S3ServiceImpl s3Service;

    @Test
    @WithMockCustomUser
    public void saveDocument() {
        var newlyCreatedDoc = documentService.saveDocument(genMultipartFile(pdf), FileCategory.CV);

        assertNotNull(newlyCreatedDoc);
        assertTrue(documentRepository.findById(newlyCreatedDoc.getId()).isPresent());
        assertNotNull(s3Service.downloadFile(newlyCreatedDoc.getPath()));
    }

    @Test
    public void getDocument_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.getDocumentById(null));
    }

    @Test
    public void getDocument_notFound() {
        assertThrows(
                AseRuntimeException.class,
                () -> documentService.getDocumentById(1000L));
    }

    @Test
    @WithMockCustomUser
    public void getDocument() {
        var documentDto = utilDocument.savedDocumentDto(FileCategory.CV);
        assertNotNull(documentService.getDocumentById(documentDto.getId()));
    }

    @Test
    @WithMockCustomUser
    public void updateDocumentType_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.updateDocumentType(null, FileCategory.PRINCIPAL_IMAGE));
    }

    @Test
    @WithMockCustomUser
    public void updateDocumentType_nullType() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.updateDocumentType(1L, null));
    }

    @Test
    @WithMockCustomUser
    public void updateDocumentType_notFound() {
        assertThrows(
                DocumentException.class,
                () -> documentService.updateDocumentType(-1L, FileCategory.PRINCIPAL_IMAGE));
    }

    @Test
    @WithMockCustomUser
    public void updateDocumentType() {
        var documentDto = utilDocument.savedDocumentDto(FileCategory.IMAGE);
        var response = documentService.updateDocumentType(documentDto.getId(), FileCategory.PRINCIPAL_IMAGE);
        assertNotNull(response);
        assertEquals(response.getId(), documentDto.getId());
        assertSame(response.getType(), FileCategory.PRINCIPAL_IMAGE);
    }

    @Test
    @WithMockCustomUser
    public void getDocumentsByIds_nullList() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.getDocumentsByIds(null));
    }

    @Test
    @WithMockCustomUser
    public void getDocumentsByIds() {
        List<Long> ids = new ArrayList<>();
        for (var category : FileCategory.values()) {
            var documentDto = utilDocument.savedDocumentDto(category);
            ids.add(documentDto.getId());
        }
        var response = documentService.getDocumentsByIds(ids);
        assertNotNull(response);
        assertEquals(response.stream().map(BaseEntityDto::getId).collect(Collectors.toList()), ids);
    }

    @Test
    public void deleteDocumentById_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.deleteDocumentById(null));
    }

    @Test
    public void deleteDocumentById_notFound() {
        assertThrows(
                DocumentException.class,
                () -> documentService.deleteDocumentById(-1L));
    }

    @Test
    @WithMockCustomUser
    public void deleteDocumentById() {
        var documentDto = utilDocument.savedDocumentDto(FileCategory.IMAGE);
        documentService.deleteDocumentById(documentDto.getId());
        assertThrows(
                DocumentException.class,
                () -> documentService.getDocumentById(documentDto.getId()));
    }

    @Test
    public void deleteDocumentsByIds_nullIds() {
        assertThrows(
                NullPointerException.class,
                () -> documentService.deleteDocumentsByIds(null));
    }

    @Test
    @WithMockCustomUser
    public void deleteDocumentsByIds() {
        List<Long> ids = new ArrayList<>();
        for (var category : FileCategory.values()) {
            var documentDto = utilDocument.savedDocumentDto(category);
            ids.add(documentDto.getId());
        }
        documentService.deleteDocumentsByIds(ids);
        assertTrue(documentService.getDocumentsByIds(ids).isEmpty());
    }
}
