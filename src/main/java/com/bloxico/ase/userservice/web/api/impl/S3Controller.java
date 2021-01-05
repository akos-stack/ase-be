package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.web.api.S3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class S3Controller implements S3Api {

    @Autowired
    private IS3Facade s3Facade;

    @Override
    public ResponseEntity<Void> uploadFile(MultipartFile file) {
        s3Facade.uploadFile(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteFile(String fileName) {
        s3Facade.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
