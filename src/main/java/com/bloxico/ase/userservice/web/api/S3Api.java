package com.bloxico.ase.userservice.web.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "s3")
public interface S3Api {

    String S3_UPLOAD   = "/s3/upload";
    String S3_DOWNLOAD = "/s3/download";
    String S3_DELETE   = "/s3/delete";

    @PostMapping(
            value = S3_UPLOAD,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_file')")
    @ApiOperation(value = "Uploads file to S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully uploaded.")
    })
    ResponseEntity<Void> uploadFile(@RequestPart(value = "file") MultipartFile file);

    @GetMapping(
            value = S3_DOWNLOAD,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'download_file')")
    @ApiOperation(value = "Downloads file from S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully downloaded.")
    })
    ResponseEntity<Resource> downloadFile(@RequestParam(value = "fileName") String fileName);

    @DeleteMapping(
            value = S3_DELETE,
            produces = {"application/json"}
    )
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_file')")
    @ApiOperation(value = "Deletes file in S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully deleted.")
    })
    ResponseEntity<Void> deleteFile(@RequestParam(value = "fileName") String fileName);
}
