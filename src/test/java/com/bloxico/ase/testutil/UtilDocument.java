package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.UploadArtworkDocumentsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;

@Component
public class UtilDocument {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private IDocumentService documentsService;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;

    public DocumentDto savedDocumentDto(FileCategory fileCategory) {
        return documentsService.saveDocument(
                genMultipartFile(fileCategory),
                fileCategory);
    }

    public UploadArtworkDocumentsRequest genUploadArtworkDocumentsRequest() {
        return genUploadArtworkDocumentsRequest(
                utilArtwork.saved(utilArtwork.genArtworkDto()).getId());
    }

    public UploadArtworkDocumentsRequest genUploadArtworkDocumentsRequest(long artworkId) {
        var request = new UploadArtworkDocumentsRequest();
        request.setArtworkId(artworkId);
        request.setDocuments(List.of(
                genMultipartFile(IMAGE),
                genMultipartFile(IMAGE)));
        request.setFileCategory(IMAGE);
        return request;
    }

}
