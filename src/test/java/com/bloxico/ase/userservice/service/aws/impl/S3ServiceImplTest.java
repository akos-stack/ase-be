package com.bloxico.ase.userservice.service.aws.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.testutil.UtilS3;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.testutil.UtilS3.findOtherCategory;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3ServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private S3ServiceImpl s3Service;
    @Autowired private UtilAuth utilAuth;
    @Autowired private UtilS3 utilS3;

    @Test
    public void validateFile_nullCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        NullPointerException.class,
                        () -> s3Service.validateFile(
                                null,
                                genMultipartFile(extension)));
    }

    @Test
    public void validateFile_nullFile() {
        for (var category : FileCategory.values())
            //noinspection ConstantConditions
            assertThrows(
                    NullPointerException.class,
                    () -> s3Service.validateFile(
                            findOtherCategory(category),
                            null));
    }

    @Test
    public void validateFile_typeNotSupportedForCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        S3Exception.class,
                        () -> s3Service.validateFile(
                                findOtherCategory(category),
                                genMultipartFile(extension)));
    }

    @Test
    public void validateFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                s3Service.validateFile(category, genMultipartFile(extension));
    }

    @Test
    public void uploadFile_nullCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        NullPointerException.class,
                        () -> s3Service.uploadFile(
                                null,
                                genMultipartFile(extension)));
    }

    @Test
    public void uploadFile_nullFile() {
        for (var category : FileCategory.values())
            //noinspection ConstantConditions
            assertThrows(
                    NullPointerException.class,
                    () -> s3Service.uploadFile(
                            findOtherCategory(category),
                            null));
    }

    @Test
    public void uploadFile_typeNotSupportedForCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertThrows(
                        S3Exception.class,
                        () -> s3Service.uploadFile(
                                findOtherCategory(category),
                                genMultipartFile(extension)));
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

    @Test
    public void validateFiles() {
        var files = utilS3.validateListOfFiles();
        var response = s3Service.validateFiles(FileCategory.IMAGE, files);
        assertEquals(response, new ArrayList<String>());
    }

    @Test
    public void validateFiles_nullFileCategory() {
        var files = utilS3.validateListOfFiles();
        assertThrows(
                NullPointerException.class,
                () ->  s3Service.validateFiles(null, files));
    }

    @Test
    public void validateFiles_nullFiles() {
        assertThrows(
                NullPointerException.class,
                () ->  s3Service.validateFiles(FileCategory.IMAGE, null));
    }

    @Test
    public void validateFiles_typeNotSupportedForCategory() {
        var files = utilS3.validateListOfFiles();
        var response = s3Service.validateFiles(FileCategory.CV, files);
        assertEquals(response, files.stream()
                                .map(MultipartFile::getOriginalFilename)
                                .collect(Collectors.toList()));
    }
}
