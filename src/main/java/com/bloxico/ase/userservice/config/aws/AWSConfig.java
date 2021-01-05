package com.bloxico.ase.userservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class AWSConfig {
    private AmazonS3 s3client;

    @Value("${s3.endpointUrl}")
    private String endpointUrl;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.accessKeyId}")
    private String accessKeyId;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials
                = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
    public String uploadFile(MultipartFile multipartFile)
            throws Exception {
        File file = convertMultiPartToFile(multipartFile);
        String fileName = generateFileName(multipartFile);
        uploadFileToS3Bucket(fileName, file);
        file.delete();
        return fileName;
    }
    public byte[] downloadFile(String fileName) throws IOException {
        S3ObjectInputStream inputStream = downloadFileFromS3Bucket(fileName);
        byte[] content = IOUtils.toByteArray(inputStream);
        return content;
    }
    public boolean deleteFileFromS3Bucket(String fileName) {
        s3client.deleteObject(bucketName, fileName);
        return true;
    }
    private void uploadFileToS3Bucket(String fileName, File file) {
        s3client.putObject(bucketName, fileName, file);
    }
    private S3ObjectInputStream downloadFileFromS3Bucket(String fileName) {
        S3Object s3Object = s3client.getObject(bucketName, fileName);
        return s3Object.getObjectContent();
    }
    private File convertMultiPartToFile(MultipartFile file)
            throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" +   multiPart.getOriginalFilename().replace(" ", "_");
    }
}
