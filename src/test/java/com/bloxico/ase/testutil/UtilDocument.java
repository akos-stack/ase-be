package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.SupportedFileExtension.pdf;

@Component
public class UtilDocument {

    @Autowired private UtilUser utilUser;
    @Autowired private IDocumentService documentsService;

    public DocumentDto savedCVDocumentDto() {
        var creatorId = utilUser.savedAdmin().getId();
        return documentsService.saveDocument(genMultipartFile(pdf), CV, creatorId);
    }
}
