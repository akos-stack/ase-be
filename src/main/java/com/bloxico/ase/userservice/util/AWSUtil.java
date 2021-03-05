package com.bloxico.ase.userservice.util;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.*;
import static java.util.Objects.requireNonNull;

@Component
public class AWSUtil {

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Autowired
    private Environment environment;

    public String uploadFile(FileCategory category, MultipartFile file) {
        requireNonNull(category);
        requireNonNull(file);
        File convFile = null;
        try {
            convFile = convertMultiPartToFile(file);
            var path = category.genFilePath(environment) + genFileName(file);
            s3client.putObject(bucketName, path, convFile);
            return path;
        } catch (Throwable ex) {
            throw FILE_UPLOAD_FAILED.newException(ex);
        } finally {
            if (convFile != null)
                //noinspection ResultOfMethodCallIgnored
                convFile.delete();
        }
    }

    public byte[] downloadFile(String path) {
        requireNonNull(path);
        try (var ob = s3client.getObject(bucketName, path);
             var in = ob.getObjectContent()) {
            return IOUtils.toByteArray(in);
        } catch (Throwable ex) {
            throw FILE_DOWNLOAD_FAILED.newException(ex);
        }
    }

    public void deleteFile(String path) {
        requireNonNull(path);
        try {
            s3client.deleteObject(bucketName, path);
        } catch (Throwable ex) {
            throw FILE_DELETE_FAILED.newException(ex);
        }
    }

    private static File convertMultiPartToFile(MultipartFile file) {
        requireNonNull(file);
        var convFile = new File(requireNonNull(file.getOriginalFilename()));
        try (var fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
            return convFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String genFileName(MultipartFile file) {
        requireNonNull(file);
        return UUID.randomUUID()
                + "."
                + FilenameUtils.getExtension(file.getOriginalFilename());
    }

}
