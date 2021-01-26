package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.api.S3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class S3Controller implements S3Api {

    @Autowired
    private IS3Facade s3Facade;

    @Override
    public ResponseEntity<Void> validateFile(FileCategory fileCategory, MultipartFile file) {
        s3Facade.validateFile(fileCategory, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileName) {
        ByteArrayResource response = s3Facade.downloadFile(fileName);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Void> deleteFile(String fileName) {
        s3Facade.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
