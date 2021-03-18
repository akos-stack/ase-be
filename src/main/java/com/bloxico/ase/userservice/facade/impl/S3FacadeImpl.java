package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.web.model.s3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ValidateFilesResponse invalidFiles(ValidateFilesRequest request) {
        log.info("S3FacadeImpl.invalidFiles - start | request: {} ", request);
        var invalidFiles = s3Service.validateFiles(
                request.getCategory(),
                request.getFiles());
        var response = new ValidateFilesResponse(invalidFiles);
        log.info("S3FacadeImpl.invalidFiles - end | request: {} ", request);
        return response;
    }

    @Override
    public ByteArrayResource downloadFile(DownloadFileRequest request) {
        log.info("S3FacadeImpl.downloadFile - start | request: {} ", request);
        var file = s3Service.downloadFile(request.getPath());
        log.info("S3FacadeImpl.downloadFile - end | request: {} ", request);
        return file;
    }

    @Override
    public void deleteFile(DeleteFileRequest request) {
        log.info("S3FacadeImpl.deleteFile - start | request: {} ", request);
        s3Service.deleteFile(request.getPath());
        log.info("S3FacadeImpl.deleteFile - end | request: {} ", request);
    }

}
