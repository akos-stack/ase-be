package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.web.api.S3Api;
import com.bloxico.ase.userservice.web.model.s3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3Controller implements S3Api {

    @Autowired
    private IS3Facade s3Facade;

    @Override
    public ResponseEntity<Void> validateFile(ValidateFileRequest request) {
        s3Facade.validateFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(DownloadFileRequest request) {
        var response = s3Facade.downloadFile(request);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Void> deleteFile(DeleteFileRequest request) {
        s3Facade.deleteFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
