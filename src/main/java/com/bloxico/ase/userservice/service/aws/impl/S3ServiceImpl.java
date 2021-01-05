package com.bloxico.ase.userservice.service.aws.impl;

import com.bloxico.ase.userservice.config.aws.AWSConfig;
import com.bloxico.ase.userservice.exception.AmazonS3Exception;
import com.bloxico.ase.userservice.service.aws.IS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class S3ServiceImpl implements IS3Service {

    private final AWSConfig awsConfig;

    public S3ServiceImpl(AWSConfig awsConfig) {
        this.awsConfig = awsConfig;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName;
        try {
            fileName = awsConfig.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmazonS3Exception(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
        return fileName;
    }

    @Override
    public boolean deleteFile(String fileName) {
        awsConfig.deleteFileFromS3Bucket(fileName);
        return true;
    }
}
