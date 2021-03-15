package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilS3;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.util.SupportedFileExtension;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.CERTIFICATE;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3FacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilS3 utilS3;
    @Autowired private S3FacadeImpl s3FacadeImpl;

    @Test
    public void invalidFiles() {
        var response = s3FacadeImpl.invalidFiles(utilS3.savedValidatedFilesRequest(FileCategory.IMAGE));
        assertTrue(response.getErrors().isEmpty());
        assertEquals(0, response.getErrors().size());
    }

    @Test
    public void invalidFiles_valid_and_invalid_file_sent() {
        var imageFile = genMultipartFile(FileCategory.IMAGE);
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(
                FileCategory.CV,
                new ArrayList(){{add(imageFile);
                                 add(genMultipartFile(FileCategory.CV));}});
        var response = s3FacadeImpl.invalidFiles(validateFilesRequest);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(imageFile.getOriginalFilename());
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY), errors);
    }

    @Test
    public void invalidFiles_file_size_exceeded() {
        var certificateFile = genMultipartFile(SupportedFileExtension.pdf, genInvalidFileBytes(CERTIFICATE));
        var certificateFile2 = genMultipartFile(SupportedFileExtension.pdf);
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(
                FileCategory.CERTIFICATE,
                new ArrayList(){{add(certificateFile); add(certificateFile2);}});
        var response = s3FacadeImpl.invalidFiles(validateFilesRequest);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(1, response.getErrors().size());
        var errors = response.getErrors().get(certificateFile.getOriginalFilename());
        assertNotNull(errors);
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED), errors);
    }

    @Test
    public void invalidFiles_typeNotSupportedForCategory() {
        var file = genMultipartFile(FileCategory.IMAGE);
        var file2 = genMultipartFile(FileCategory.IMAGE);
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(
                CERTIFICATE,
                new ArrayList(){{add(file); add(file2);}});
        var response = s3FacadeImpl.invalidFiles(validateFilesRequest);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(2, response.getErrors().size());
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.getErrors().get(file.getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY),
                response.getErrors().get(file2.getOriginalFilename()));
    }

    @Test
    public void invalidFiles_allErrors() {
        var file = genMultipartFile(SupportedFileExtension.pdf,
                genInvalidFileBytes(CERTIFICATE));
        var file2 = genMultipartFile(SupportedFileExtension.pdf,
                genInvalidFileBytes(CERTIFICATE));
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(
                FileCategory.IMAGE,
                new ArrayList(){{add(file); add(file2);}});
        var response = s3FacadeImpl.invalidFiles(validateFilesRequest);
        assertFalse(response.getErrors().isEmpty());
        assertEquals(2, response.getErrors().size());
        assertNotNull(response.getErrors().get(file.getOriginalFilename()));
        assertNotNull(response.getErrors().get(file2.getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY,
                ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED),
                response.getErrors().get(file.getOriginalFilename()));
        assertEquals(Set.of(ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY,
                ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED),
                response.getErrors().get(file2.getOriginalFilename()));
    }

    @Test
    public void invalidFiles_nullFileCategory() {
        assertThrows(
                NullPointerException.class,
                () ->  utilS3.savedValidatedFilesRequest(null));
    }

    @Test
    public void invalidFiles_nullFiles() {
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(FileCategory.IMAGE, null);
        assertThrows(
                NullPointerException.class,
                () ->  s3FacadeImpl.invalidFiles(validateFilesRequest));
    }
}

