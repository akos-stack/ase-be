package com.bloxico.ase.userservice.service.document;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Documents.DOCUMENT_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Service
public class DocumentServiceImpl implements IDocumentService {

    private final IS3Service s3Service;
    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(IS3Service s3Service, DocumentRepository documentRepository) {
        this.s3Service = s3Service;
        this.documentRepository = documentRepository;
    }

    @Override
    public DocumentDto saveDocument(MultipartFile file, FileCategory type, long principalId) {
        var path = s3Service.uploadFile(type, file);
        var document = new Document();
        document.setName(file.getOriginalFilename());
        document.setPath(path);
        document.setType(type);
        document.setCreatorId(principalId);
        return MAPPER.toDto(documentRepository.save(document));
    }

    @Override
    public ByteArrayResource getDocumentById(Long id) {
        requireNonNull(id);
        var document = documentRepository.findById(id).orElseThrow(DOCUMENT_NOT_FOUND::newException);
        return s3Service.downloadFile(document.getPath());
    }
}
