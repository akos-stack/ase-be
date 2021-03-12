package com.bloxico.ase.userservice.service.document.impl;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Document.DOCUMENT_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class DocumentServiceImpl implements IDocumentService {

    private final IS3Service s3Service;
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(IS3Service s3Service, DocumentRepository documentRepository) {
        this.s3Service = s3Service;
        this.documentRepository = documentRepository;
    }

    @Override
    public DocumentDto saveDocument(MultipartFile file, FileCategory type) {
        return saveDocument(file, type, null);
    }

    @Override
    public DocumentDto saveDocument(MultipartFile file, FileCategory type, Long principalId) {
        log.info("DocumentServiceImpl.saveDocument - start | file: {}, fileCategory: {}", file, type);
        var path = s3Service.uploadFile(type, file);
        var document = new Document();
        document.setPath(path);
        document.setType(type);
        if (principalId != null)
            document.setCreatorId(principalId);
        var documentDto = MAPPER.toDto(documentRepository.save(document));
        log.info("DocumentServiceImpl.saveDocument - end | file: {}, fileCategory: {}", file, type);
        return documentDto;
    }

    @Override
    public ByteArrayResource getDocumentById(Long id) {
        log.info("DocumentServiceImpl.getDocumentById - start | id: {} ", id);
        requireNonNull(id);
        var document = documentRepository
                .findById(id)
                .map(Document::getPath)
                .map(s3Service::downloadFile)
                .orElseThrow(DOCUMENT_NOT_FOUND::newException);
        log.info("DocumentServiceImpl.getDocumentById - end | id: {} ", id);
        return document;
    }

    @Override
    public DocumentDto updateDocumentType(Long id, FileCategory type) {
        log.info("DocumentServiceImpl.updateDocumentType - start | id: {}, type: {}", id, type);
        requireNonNull(id);
        requireNonNull(type);
        var document = documentRepository.findById(id).orElseThrow(DOCUMENT_NOT_FOUND::newException);
        document.setType(type);
        document = documentRepository.save(document);
        log.info("DocumentServiceImpl.updateDocumentType - end | id: {}, type: {}", id, type);
        return MAPPER.toDto(document);
    }

    @Override
    public List<DocumentDto> getDocumentsById(List<Long> ids) {
        log.info("DocumentServiceImpl.findAllByIds - start | ids: {}", ids);
        var documents = documentRepository.findAllById(ids);
        log.info("DocumentServiceImpl.findAllByIds - start | ids: {}", ids);
        return documents.stream().map(MAPPER::toDto).collect(Collectors.toList());
    }
}
