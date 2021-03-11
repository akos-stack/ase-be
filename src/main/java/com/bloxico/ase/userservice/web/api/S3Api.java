package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
import io.swagger.annotations.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "s3")
public interface S3Api {

    String S3_INVALID_FILES = "/s3/invalid_files";
    String S3_DOWNLOAD      = "/s3/download";
    String S3_DELETE        = "/s3/delete";

    @PostMapping(
            value = S3_INVALID_FILES,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_file')")
    @ApiOperation(value = "Validate multiple files before uploading to S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned list of incorrect files and error codes or empty list.")
    })
    ResponseEntity<ValidateFilesResponse> invalidFiles(@Valid ValidateFilesRequest validateFilesRequest);

    @GetMapping(
            value = S3_DOWNLOAD,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'download_file')")
    @ApiOperation(value = "Downloads file from S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully downloaded.")
    })
    ResponseEntity<Resource> downloadFile(@RequestParam(value = "fileName") String path);

    @DeleteMapping(
            value = S3_DELETE,
            produces = {"application/json"}
    )
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_file')")
    @ApiOperation(value = "Deletes file in S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully deleted.")
    })
    ResponseEntity<Void> deleteFile(@RequestParam(value = "fileName") String path);

}
