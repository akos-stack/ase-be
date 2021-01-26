package com.bloxico.ase.userservice.service.aws.impl;

import com.bloxico.ase.userservice.exception.AmazonS3Exception;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.AWSUtil;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class S3ServiceImpl implements IS3Service {

    private final AWSUtil awsUtil;
    private final Environment environment;

    @Autowired
    public S3ServiceImpl(AWSUtil awsUtil, Environment environment) {
        this.awsUtil = awsUtil;
        this.environment = environment;
    }

    @Override
    public void validateFile(FileCategory fileCategory, MultipartFile file) {
        log.debug("S3ServiceImpl.validateFile - start | fileCategory: {}, file: {}", fileCategory, file.getName());
        fileCategory.validate(file, environment);
        log.debug("S3ServiceImpl.validateFile - end | fileCategory: {}, file: {}", fileCategory, file.getName());
    }

    @Override
    public String uploadFile(FileCategory fileCategory, MultipartFile file) {
        log.debug("S3ServiceImpl.uploadFile - start | file: {}", file.getName());
        fileCategory.validate(file, environment);
        try {
            String fileUploadPath = awsUtil.uploadFile(fileCategory, file);
            log.debug("S3ServiceImpl.uploadFile - end | file: {}", file.getName());
            return fileUploadPath;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmazonS3Exception(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }

    @Override
    public ByteArrayResource downloadFile(String fileName) {
        log.debug("S3ServiceImpl.downloadFile - start | fileName: {}", fileName);
        ByteArrayResource byteArrayResource;
        try {
            byteArrayResource = new ByteArrayResource(awsUtil.downloadFile(fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AmazonS3Exception(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
        log.debug("S3ServiceImpl.downloadFile - end | fileName: {}", fileName);
        return byteArrayResource;
    }

    @Override
    public boolean deleteFile(String fileName) {
        log.debug("S3ServiceImpl.deleteFile - start | fileName: {}", fileName);
        awsUtil.deleteFileFromS3Bucket(fileName);
        log.debug("S3ServiceImpl.deleteFile - end | fileName: {}", fileName);
        return true;
    }

}
