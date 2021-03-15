package com.bloxico.ase.userservice.service.aws.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilS3;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.UtilS3.findOtherCategory;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3ServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private S3ServiceImpl s3Service;
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
        var response = s3Service.validateFiles(FileCategory.IMAGE,
                utilS3.savedListOfFiles(FileCategory.IMAGE));
        assertTrue(response.isEmpty());
        assertEquals(0, response.size());
    }

    @Test
    public void validateFiles_nullFileCategory() {
        assertThrows(
                NullPointerException.class,
                () ->  s3Service.validateFiles(null, utilS3.savedListOfFiles(FileCategory.CV)));
    }

    @Test
    public void validateFiles_nullFiles() {
        assertThrows(
                NullPointerException.class,
                () ->  s3Service.validateFiles(FileCategory.IMAGE, null));
    }

    @Test
    public void validateFiles_typeNotSupportedForCategory() {
        var files = utilS3.savedListOfFiles(FileCategory.IMAGE);
        var response = s3Service.validateFiles(FileCategory.CV, files);
        assertFalse(response.isEmpty());
        assertEquals(2, response.size());
        assertNotNull(response.get(files.get(0).getOriginalFilename()));
        assertNotNull(response.get(files.get(1).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.get(files.get(0).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.get(files.get(1).getOriginalFilename()));
    }

    @Test
    public void validateFiles_fileSizeExceeded() {
        var files = utilS3.savedListOfInvalidFiles(FileCategory.CV);
        var response = s3Service.validateFiles(FileCategory.CV, files);
        assertFalse(response.isEmpty());
        assertEquals(2, response.size());
        assertNotNull(response.get(files.get(0).getOriginalFilename()));
        assertNotNull(response.get(files.get(1).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED),
                response.get(files.get(0).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED),
                response.get(files.get(1).getOriginalFilename()));
    }

    @Test
    public void validateFiles_allErrors() {
        var files = utilS3.savedListOfInvalidFiles(FileCategory.IMAGE);
        var response = s3Service.validateFiles(FileCategory.CV, files);
        assertFalse(response.isEmpty());
        assertEquals(2, response.size());
        assertNotNull(response.get(files.get(0).getOriginalFilename()));
        assertNotNull(response.get(files.get(1).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED,
                ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.get(files.get(0).getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED,
                ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.get(files.get(1).getOriginalFilename()));
    }

}
