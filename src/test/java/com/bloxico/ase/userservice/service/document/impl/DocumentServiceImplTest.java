package com.bloxico.ase.userservice.service.document.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilDocument;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DocumentServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilDocument utilDocument;
    @Autowired private DocumentServiceImpl documentService;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private S3ServiceImpl s3Service;

    @Test
    public void saveDocument() {
        var creatorId = utilUser.savedAdmin().getId();
        var newlyCreatedDoc = documentService.saveDocument(genMultipartFile(pdf), FileCategory.CV, creatorId);

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
                () -> documentService.getDocumentById(1000l));
    }

    @Test
    public void getDocument() {
        var documentDto = utilDocument.savedCVDocumentDto();
        assertNotNull(documentService.getDocumentById(documentDto.getId()));
    }
}
