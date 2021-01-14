package com.bloxico.ase.userservice.service.aws.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertTrue;

public class S3ServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired
    private S3ServiceImpl s3Service;

    @Test
    public void upload_success() {
        MultipartFile multipartFile = new MockMultipartFile("some.txt","some.txt", "text/plain", "Hello".getBytes());
        String fileName = s3Service.uploadFile(multipartFile);
        assertTrue(amazonS3.getObject(bucketName, fileName) != null);
    }

    @Test(expected = com.bloxico.ase.userservice.exception.AmazonS3Exception.class)
    public void upload_failOnTypeNotSupported() {
        MultipartFile multipartFile = new MockMultipartFile("some.zip","some.zip", "application/zip", "Hello".getBytes());
        s3Service.uploadFile(multipartFile);
    }

    @Test
    public void download_success() {
        MultipartFile multipartFile = new MockMultipartFile("some.txt","some.txt", "text/plain", "Hello".getBytes());
        String fileName = s3Service.uploadFile(multipartFile);
        ByteArrayResource resource = s3Service.downloadFile(fileName);
        assertTrue(resource != null);
    }

    @Test(expected = AmazonS3Exception.class)
    public void delete_success() {
        MultipartFile multipartFile = new MockMultipartFile("some.txt","some.txt", "text/plain", "Hello".getBytes());
        String fileName = s3Service.uploadFile(multipartFile);
        s3Service.deleteFile(fileName);
        amazonS3.getObject(bucketName, fileName);
    }
}
