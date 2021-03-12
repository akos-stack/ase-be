package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.s3.*;
import io.swagger.annotations.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "s3")
public interface S3Api {

    // @formatter:off
    String S3_VALIDATE = "/s3/validate";
    String S3_DOWNLOAD = "/s3/download";
    String S3_DELETE   = "/s3/delete";
    // @formatter:on

    @PostMapping(
            value = S3_VALIDATE,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'validate_file')")
    @ApiOperation(value = "Validates file for S3 bucket upload.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File is valid.")
    })
    ResponseEntity<Void> validateFile(@Valid ValidateFileRequest request);

    @GetMapping(
            value = S3_DOWNLOAD,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'download_file')")
    @ApiOperation(value = "Downloads file from S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully downloaded.")
    })
    ResponseEntity<Resource> downloadFile(@Valid DownloadFileRequest request);

    @DeleteMapping(
            value = S3_DELETE,
            produces = {"application/json"}
    )
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_file')")
    @ApiOperation(value = "Deletes file in S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully deleted.")
    })
    ResponseEntity<Void> deleteFile(@Valid DeleteFileRequest request);

}
