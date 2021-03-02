package com.bloxico.ase.userservice.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.userservice.exception.S3Exception;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static com.bloxico.ase.testutil.Util.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class AWSUtilTest extends AbstractSpringTestWithAWS {

    @Autowired private AWSUtil awsUtil;
    @Autowired private AmazonS3 s3client;
    @Autowired private Environment environment;

    @Test
    public void uploadFile_nullArguments() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                assertThrows(
                        NullPointerException.class,
                        () -> awsUtil.uploadFile(category, null));
                assertThrows(
                        NullPointerException.class,
                        () -> awsUtil.uploadFile(null, genMultipartFile(extension)));
            }
    }

    @Test
    public void uploadFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = awsUtil.uploadFile(category, genMultipartFile(extension));
                var path = category.genFilePath(environment) + fileName;
                assertNotNull(s3client.putObject(bucketName, path, fileName));
            }
    }

    @Test
    public void uploadFile_typeNotSupportedForCategory() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions())
                assertNotNull(
                        awsUtil.uploadFile(
                                randOtherEnumConst(category),
                                genMultipartFile(extension)));
    }

    @Test
    public void downloadFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> awsUtil.downloadFile(null));
    }

    @Test
    public void downloadFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> awsUtil.downloadFile(genUUID()));
    }

    @Test
    public void downloadFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = awsUtil.uploadFile(category, genMultipartFile(extension));
                assertNotNull(awsUtil.downloadFile(fileName));
            }
    }

    @Test
    public void deleteFile_nullPath() {
        assertThrows(
                NullPointerException.class,
                () -> awsUtil.deleteFile(null));
    }

    @Test
    public void deleteFile_notFound() {
        assertThrows(
                S3Exception.class,
                () -> awsUtil.deleteFile(genUUID()));
    }

    @Test
    public void deleteFile() {
        for (var category : FileCategory.values())
            for (var extension : category.getSupportedFileExtensions()) {
                var fileName = awsUtil.uploadFile(category, genMultipartFile(extension));
                assertNotNull(awsUtil.downloadFile(fileName));
                awsUtil.deleteFile(fileName);
                assertThrows(
                        AmazonS3Exception.class,
                        () -> amazonS3.getObject(bucketName, fileName));
            }
    }

}