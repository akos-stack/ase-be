package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class S3FacadeImpl implements IS3Facade {

    private final IS3Service s3Service;

    public S3FacadeImpl(IS3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public boolean validateFile(FileCategory fileCategory, MultipartFile file) {
        log.info("S3FacadeImpl.validateFile - start | fileCategory: {}, file: {} ", fileCategory, file.getName());
        var response = s3Service.validateFile(fileCategory, file);
        log.info("S3FacadeImpl.validateFile - end | fileCategory: {}, file: {} ", fileCategory, file.getName());
        return response;
    }

    @Override
    public ByteArrayResource downloadFile(String fileName) {
        log.info("S3FacadeImpl.downloadFile - start | fileName: {} ", fileName);
        var file = s3Service.downloadFile(fileName);
        log.info("S3FacadeImpl.downloadFile - end | fileName: {} ", fileName);
        return file;
    }

    @Override
    public void deleteFile(String fileName) {
        log.info("S3FacadeImpl.deleteFile - start | fileName: {} ", fileName);
        s3Service.deleteFile(fileName);
        log.info("S3FacadeImpl.deleteFile - end | fileName: {} ", fileName);
    }
}
