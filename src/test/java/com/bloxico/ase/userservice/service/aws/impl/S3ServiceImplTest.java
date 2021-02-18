package com.bloxico.ase.userservice.service.aws.impl;

import com.amazonaws.services.alexaforbusiness.model.NotFoundException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.util.FileCategory;
import mockit.internal.ClassFile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.testutil.Util.randOtherEnumConst;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3ServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private S3ServiceImpl s3Service;

    // TODO-TEST validateFile_nullArguments
    @Test
    public void validateFile_nullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.validateFile(
                        randOtherEnumConst(null),
                        genMultipartFile(null)));
    }

    @Test
    public void validateFile_typeNotSupportedForCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        S3Exception.class,
                        () -> s3Service.validateFile(
                                randOtherEnumConst(category),
                                genMultipartFile(extension)));
    }

    @Test
    public void validateFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                s3Service.validateFile(category, genMultipartFile(extension));
    }

    // TODO-TEST uploadFile_nullArguments
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
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        S3Exception.class,
                        () -> s3Service.uploadFile(
                                randOtherEnumConst(category),
                                genMultipartFile(extension)));
    }

    // TODO-TEST downloadFile_nullPath
    @Test
    public void downloadFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.downloadFile(null));
    }

    // TODO-TEST downloadFile_notFound
    @Test
    public void downloadFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.downloadFile(""));
    }

    @Test
    public void downloadFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = s3Service.uploadFile(category, genMultipartFile(extension));
                assertNotNull(s3Service.downloadFile(fileName));
            }
    }

    // TODO-TEST deleteFile_nullPath
    @Test
    public void deleteFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> s3Service.deleteFile(null));
    }

    // TODO-TEST deleteFile_notFound
    @Test
    public void deleteFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> s3Service.deleteFile("test_path"));
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
