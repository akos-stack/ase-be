package com.bloxico.ase.userservice.service.aws.impl;

import com.bloxico.ase.userservice.service.aws.IS3Service;
import com.bloxico.ase.userservice.util.AWSUtil;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Objects.requireNonNull;

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
    public void validateFile(FileCategory category, MultipartFile file) {
        log.debug("S3ServiceImpl.validateFile - start | category: {}, file: {}", category, file.getName());
        requireNonNull(category);
        requireNonNull(file);
        category.validate(file, environment);
        log.debug("S3ServiceImpl.validateFile - end | category: {}, file: {}", category, file.getName());
    }

    @Override
    public String uploadFile(FileCategory category, MultipartFile file) {
        log.debug("S3ServiceImpl.uploadFile - start | category: {}, file: {}", category, file.getName());
        requireNonNull(category);
        requireNonNull(file);
        validateFile(category, file);
        var fileUploadPath = awsUtil.uploadFile(category, file);
        log.debug("S3ServiceImpl.uploadFile - end | category: {}, file: {}", category, file.getName());
        return fileUploadPath;
    }

    @Override
    public ByteArrayResource downloadFile(String path) {
        log.debug("S3ServiceImpl.downloadFile - start | path: {}", path);
        requireNonNull(path);
        var file = new ByteArrayResource(awsUtil.downloadFile(path));
        log.debug("S3ServiceImpl.downloadFile - end | path: {}", path);
        return file;
    }

    @Override
    public void deleteFile(String path) {
        log.debug("S3ServiceImpl.deleteFile - start | path: {}", path);
        requireNonNull(path);
        awsUtil.deleteFile(path);
        log.debug("S3ServiceImpl.deleteFile - end | path: {}", path);
    }

}
