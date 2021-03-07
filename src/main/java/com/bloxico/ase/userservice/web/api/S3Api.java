package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import io.swagger.annotations.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Api(value = "s3")
public interface S3Api {

    String S3_VALIDATE = "/s3/validate";
    String S3_DOWNLOAD = "/s3/download";
    String S3_DELETE   = "/s3/delete";
    String S3_VALIDATE_FILES   = "/s3/validate/files";

    @PostMapping(
            value = S3_VALIDATE,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_file')")
    @ApiOperation(value = "Uploads file to S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "File successfully uploaded.")
    })
    ResponseEntity<Void> validateFile(@RequestParam(name = "fileCategory") FileCategory category,
                                      @RequestPart(value = "file") MultipartFile file);

    @PostMapping(
            value = S3_VALIDATE_FILES,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_files')")
    @ApiOperation(value = "Upload multiple files to S3 bucket.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Files successfully uploaded."),
            @ApiResponse(code = 400, message = "Invalid files.")
    })
    ResponseEntity<List<String>> validateFiles(ValidateFilesRequest validateFilesRequest);

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
