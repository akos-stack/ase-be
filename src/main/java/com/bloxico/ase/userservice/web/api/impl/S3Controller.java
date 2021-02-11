package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.api.S3Api;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> validateFile(FileCategory category, MultipartFile file) {
        s3Facade.validateFile(category, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String path) {
        var response = s3Facade.downloadFile(path);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Void> deleteFile(String path) {
        s3Facade.deleteFile(path);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
