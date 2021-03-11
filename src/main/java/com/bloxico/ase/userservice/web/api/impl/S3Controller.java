package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IS3Facade;
import com.bloxico.ase.userservice.web.api.S3Api;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
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
    public ResponseEntity<ValidateFilesResponse> invalidFiles(ValidateFilesRequest validateFilesRequest) {
        var response = s3Facade.invalidFiles(validateFilesRequest);
        return ResponseEntity.ok(response);
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
