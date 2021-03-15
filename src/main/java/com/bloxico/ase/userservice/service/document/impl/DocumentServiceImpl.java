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

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Document.DOCUMENT_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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
        return saveDocument(file, type, 0);
    }

    @Override
    public DocumentDto saveDocument(MultipartFile file, FileCategory type, long principalId) {
        log.info("DocumentServiceImpl.saveDocument - start | file: {}, fileCategory: {}", file, type);
        var path = s3Service.uploadFile(type, file);
        var document = new Document();
        document.setPath(path);
        document.setType(type);
        if (principalId > 0)
            document.setCreatorId(principalId);
        var documentDto = MAPPER.toDto(documentRepository.save(document));
        log.info("DocumentServiceImpl.saveDocument - end | file: {}, fileCategory: {}", file, type);
        return documentDto;
    }

    @Override
    public ByteArrayResource findDocumentById(long id) {
        log.info("DocumentServiceImpl.getDocumentById - start | id: {} ", id);
        var document = documentRepository
                .findById(id)
                .map(Document::getPath)
                .map(s3Service::downloadFile)
                .orElseThrow(DOCUMENT_NOT_FOUND::newException);
        log.info("DocumentServiceImpl.getDocumentById - end | id: {} ", id);
        return document;
    }

    @Override
    public DocumentDto updateDocumentType(long id, FileCategory type) {
        log.info("DocumentServiceImpl.updateDocumentType - start | id: {}, type: {}", id, type);
        requireNonNull(type);
        var documentDto = documentRepository
                .findById(id)
                .map(doto(doc -> doc.setType(type)))
                .map(documentRepository::saveAndFlush)
                .map(MAPPER::toDto)
                .orElseThrow(DOCUMENT_NOT_FOUND::newException);
        log.info("DocumentServiceImpl.updateDocumentType - end | id: {}, type: {}", id, type);
        return documentDto;
    }

    @Override
    public List<DocumentDto> findDocumentsByIds(List<Long> ids) {
        log.info("DocumentServiceImpl.findAllByIds - start | ids: {}", ids);
        requireNonNull(ids);
        var documents = documentRepository
                .findAllById(ids)
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
        log.info("DocumentServiceImpl.findAllByIds - start | ids: {}", ids);
        return documents;
    }

    @Override
    public List<DocumentDto> findDocumentsByArtworkId(long id) {
        log.info("DocumentServiceImpl.findDocumentsByArtworkId - start | id: {}", id);
        var documents = documentRepository
                .findAllByArtworkId(id)
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
        log.info("DocumentServiceImpl.findDocumentsByArtworkId - end | id: {}", id);
        return documents;
    }

    @Override
    public void deleteDocumentById(long id) {
        log.info("DocumentServiceImpl.deleteDocumentById - start | id: {} ", id);
        var document = documentRepository
                .findById(id)
                .orElseThrow(DOCUMENT_NOT_FOUND::newException);
        s3Service.deleteFile(document.getPath());
        documentRepository.delete(document);
        log.info("DocumentServiceImpl.deleteDocumentById - end | id: {} ", id);
    }

    @Override
    public void deleteDocumentsByIds(List<Long> id) {
        log.info("DocumentServiceImpl.deleteDocumentById - start | id: {} ", id);
        requireNonNull(id);
        var documents = documentRepository
                .findAllById(id);
        documents.forEach(document -> s3Service.deleteFile(document.getPath()));
        documentRepository.deleteAll(documents);
        log.info("DocumentServiceImpl.deleteDocumentById - end | id: {} ", id);
    }
}
