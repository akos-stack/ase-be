package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genMultipartFile;

@Component
public class UtilDocument {

    @Autowired private UtilUser utilUser;
    @Autowired private IDocumentService documentsService;

    public DocumentDto savedDocumentDto(FileCategory fileCategory) {
        var creatorId = utilUser.savedAdmin().getId();
        return documentsService.saveDocument(genMultipartFile(fileCategory.getSupportedFileExtensions().stream().findFirst().get()), fileCategory, creatorId);
    }
}
