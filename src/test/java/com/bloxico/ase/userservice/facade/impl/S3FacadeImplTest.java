package com.bloxico.ase.userservice.facade.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilS3;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3FacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilS3 utilS3;
    @Autowired private S3FacadeImpl s3FacadeImpl;

    @Test
    public void validateFiles() {
        var response = s3FacadeImpl.validateFiles(utilS3.savedValidatedFilesRequest(FileCategory.IMAGE));
        assertTrue(response.isEmpty());
    }

    @Test
    public void validateFiles_nullFileCategory() {
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(null);
        assertThrows(
                AmazonS3Exception.class,
                () ->  s3FacadeImpl.validateFiles(validateFilesRequest));
    }

    @Test
    public void validateFiles_typeNotSupportedForCategory() {
        var validateFilesRequest = utilS3.savedValidatedFilesRequest(FileCategory.CV);
        var response = s3FacadeImpl.validateFiles(validateFilesRequest);
        assertFalse(response.isEmpty());
        assertEquals(response, validateFilesRequest.getFiles()
                .stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList()));
    }

}

