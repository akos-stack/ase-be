package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.api.S3Api;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<List<String>> validateFiles(ValidateFilesRequest validateFilesRequest) {
        var response = s3Facade.validateFiles(validateFilesRequest);
        if(!response.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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