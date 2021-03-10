package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class S3FacadeImpl implements IS3Facade {

    private final IS3Service s3Service;

    @Autowired
    public S3FacadeImpl(IS3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public void validateFile(FileCategory category, MultipartFile file) {
        log.info("S3FacadeImpl.validateFile - start | category: {}, file: {} ", category, file.getName());
        s3Service.validateFile(category, file);
        log.info("S3FacadeImpl.validateFile - end | category: {}, file: {} ", category, file.getName());
    }

    @Override
    public ValidateFilesResponse invalidFiles(ValidateFilesRequest validateFilesRequest) {
        log.info("S3FacadeImpl.validateFiles - start | validateFilesRequest: {} ", validateFilesRequest);
        var invalidFiles = s3Service.validateFiles(
                validateFilesRequest.getFileCategory(),
                validateFilesRequest.getFiles());
        var response = new ValidateFilesResponse(invalidFiles);
        log.info("S3FacadeImpl.validateFiles - end | validateFilesRequest: {} ", validateFilesRequest);
        return response;
    }

    @Override
    public ByteArrayResource downloadFile(String path) {
        log.info("S3FacadeImpl.downloadFile - start | path: {} ", path);
        var file = s3Service.downloadFile(path);
        log.info("S3FacadeImpl.downloadFile - end | path: {} ", path);
        return file;
    }

    @Override
    public void deleteFile(String path) {
        log.info("S3FacadeImpl.deleteFile - start | path: {} ", path);
        s3Service.deleteFile(path);
        log.info("S3FacadeImpl.deleteFile - end | path: {} ", path);
    }

}
