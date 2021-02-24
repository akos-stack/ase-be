package com.bloxico.ase.userservice.service.aws.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.userservice.entity.artwork.metadata.Category;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.util.SupportedFileExtension;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.bloxico.ase.testutil.Util.*;
import static org.junit.Assert.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3ServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private S3ServiceImpl s3Service;

    @Test
    public void validateFile_nullArguments() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                assertThrows(
                        NullPointerException.class,
                        () -> s3Service.validateFile(
                                randOtherEnumConst(null),
                                genMultipartFile(extension)));
                assertThrows(
                        NullPointerException.class,
                        () -> s3Service.validateFile(
                                randOtherEnumConst(category),
                                genMultipartFile(null)));
            }
    }

    @Test
    public void validateFile_typeNotSupportedForCategory() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.validateFile(
                        FileCategory.CV,
                        genMultipartFile(SupportedFileExtension.png)));
    }

    @Test
    public void validateFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                s3Service.validateFile(category, genMultipartFile(extension));
    }

    @Test
    public void uploadFile_nullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.uploadFile(null, null));
    }

    @Test
    public void uploadFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = s3Service.uploadFile(category, genMultipartFile(extension));
                assertNotNull(amazonS3.getObject(bucketName, fileName));
            }
    }

    @Test
    public void uploadFile_typeNotSupportedForCategory() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.uploadFile(
                        FileCategory.CV,
                        genMultipartFile(SupportedFileExtension.png)));
    }

    @Test
    public void downloadFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.downloadFile(null));
    }

    @Test
    public void downloadFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.downloadFile(genUUID()));
    }

    @Test
    public void downloadFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = s3Service.uploadFile(category, genMultipartFile(extension));
                assertNotNull(s3Service.downloadFile(fileName));
            }
    }

    @Test
    public void deleteFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.deleteFile(null));
    }

    @Test
    public void deleteFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.deleteFile(genUUID()));
    }

    @Test
    public void deleteFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = s3Service.uploadFile(category, genMultipartFile(extension));
                assertNotNull(s3Service.downloadFile(fileName));
                s3Service.deleteFile(fileName);
                assertThrows(
                        AmazonS3Exception.class,
                        () -> amazonS3.getObject(bucketName, fileName));
            }
    }

}
