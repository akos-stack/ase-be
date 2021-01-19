package com.bloxico.ase.userservice.service.aws.impl;

import com.bloxico.ase.userservice.exception.AmazonS3Exception;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.AWSUtil;
import com.bloxico.ase.userservice.util.SupportedFileTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class S3ServiceImpl implements IS3Service {

    private final AWSUtil awsUtil;

    public S3ServiceImpl(AWSUtil awsUtil) {
        this.awsUtil = awsUtil;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        log.debug("S3ServiceImpl.uploadFile - start | file: {}", file.getName());
        SupportedFileTypes.checkIsSupported(FilenameUtils.getExtension(file.getOriginalFilename()));
        String fileName;
        try {
            fileName = awsUtil.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmazonS3Exception(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
        log.debug("S3ServiceImpl.uploadFile - end | file: {}", file.getName());
        return fileName;
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
