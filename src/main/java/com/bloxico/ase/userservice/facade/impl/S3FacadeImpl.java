package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.service.aws.IS3Service;
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
    public void uploadFile(MultipartFile file) {
        log.info("S3FacadeImpl.uploadFile - start | file: {} ", file.getName());
        s3Service.uploadFile(file);
        log.info("S3FacadeImpl.uploadFile - end | file: {} ", file.getName());
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
