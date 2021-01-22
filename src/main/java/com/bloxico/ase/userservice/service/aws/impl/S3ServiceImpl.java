package com.bloxico.ase.userservice.service.aws.impl;

import com.bloxico.ase.userservice.exception.AmazonS3Exception;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.AWSUtil;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class S3ServiceImpl implements IS3Service {

    private final AWSUtil awsUtil;
    private final FileUtil fileUtil;

    public S3ServiceImpl(AWSUtil awsUtil, FileUtil fileUtil) {
        this.awsUtil = awsUtil;
        this.fileUtil = fileUtil;
    }

    @Override
    public boolean validateFile(FileCategory fileCategory, MultipartFile file) {
        log.debug("S3ServiceImpl.validateFile - start | fileCategory: {}, file: {}",fileCategory, file.getName());
        var valid = fileUtil.validate(fileCategory, file);
        log.debug("S3ServiceImpl.validateFile - end | fileCategory: {}, file: {}", fileCategory, file.getName());
        return valid;
    }

    @Override
    public String uploadFile(FileCategory fileCategory, MultipartFile file) {
        log.debug("S3ServiceImpl.uploadFile - start | file: {}", file.getName());
        fileUtil.validate(fileCategory, file);
        String fileUploadPath;
        try {
            fileUploadPath = awsUtil.uploadFile(fileCategory, file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmazonS3Exception(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
        log.debug("S3ServiceImpl.uploadFile - end | file: {}", file.getName());
        return fileUploadPath;
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
