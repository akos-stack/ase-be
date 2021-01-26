package com.bloxico.ase.userservice.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
public class AWSUtil {

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucketName}")
    private String bucketName;
    
    @Autowired
    private Environment environment;

    public String uploadFile(FileCategory fileCategory, MultipartFile multipartFile) {
        var file = convertMultiPartToFile(multipartFile);
        var rootPath = fileCategory.generateFilePath(environment);
        var filePath = rootPath + generateFileName(multipartFile);
        uploadFileToS3Bucket(filePath, file);
        file.delete();
        return filePath;
    }
    
    public String uploadFiles(FileCategory fileCategory, List<MultipartFile> multipartFileList) {
        var files = convertMultipartToFiles(multipartFileList);
        var rootPath = fileCategory.generateFilePath(environment);
        files.forEach(file -> {
            String filePath = rootPath + generateFileName(file);
            uploadFileToS3Bucket(filePath, file);
            file.delete();
        });
        return rootPath;
    }

    public byte[] downloadFile(String filePath) throws IOException {
        S3ObjectInputStream inputStream = downloadFileFromS3Bucket(filePath);
        return IOUtils.toByteArray(inputStream);
    }

    public void deleteFileFromS3Bucket(String filePath) {
        s3client.deleteObject(bucketName, filePath);
    }

    private void uploadFileToS3Bucket(String filePath, File file) {
        s3client.putObject(bucketName, filePath, file);
    }

    private S3ObjectInputStream downloadFileFromS3Bucket(String filePath) {
        S3Object s3Object = s3client.getObject(bucketName, filePath);
        return s3Object.getObjectContent();
    }

    private File convertMultiPartToFile(MultipartFile file) {
        var convFile = new File(requireNonNull(file.getOriginalFilename()));
        try (var fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convFile;
    }

    private List<File> convertMultipartToFiles(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream().map(this::convertMultiPartToFile).collect(Collectors.toList());
    }

    private String generateFileName(MultipartFile multipartFile) {
        return UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    }

    private String generateFileName(File file) {
        return UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getName());
    }

}
