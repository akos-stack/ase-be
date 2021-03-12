package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.*;
import io.swagger.annotations.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "registration")
public interface UserRegistrationApi {

    // @formatter:off
    String REGISTRATION_ENDPOINT                      = "/user/registration";
    String REGISTRATION_CONFIRM_ENDPOINT              = "/user/registration/confirm";
    String REGISTRATION_TOKEN_REFRESH_ENDPOINT        = "/user/registration/token/refresh";
    String REGISTRATION_TOKEN_RESEND_ENDPOINT         = "/user/registration/token/resend";
    String REGISTRATION_EVALUATOR_INVITATION          = "/user/registration/evaluator/invitation";
    String REGISTRATION_EVALUATOR_INVITATION_CHECK    = "/user/registration/evaluator/invitation/check/{token}";
    String REGISTRATION_EVALUATOR_INVITATION_RESEND   = "/user/registration/evaluator/invitation/resend";
    String REGISTRATION_EVALUATOR_INVITATION_WITHDRAW = "/user/registration/evaluator/invitation/withdraw";
    String REGISTRATION_EVALUATOR_SUBMIT              = "/user/registration/evaluator/submit";
    String REGISTRATION_ART_OWNER_SUBMIT              = "/user/registration/art-owner/submit";
    String REGISTRATION_EVALUATOR_REQUEST             = "/user/registration/evaluator/request";
    String REGISTRATION_EVALUATOR_SEARCH              = "/user/registration/evaluator/search";
    String REGISTRATION_EVALUATOR_RESUME_DOWNLOAD     = "/user/registration/evaluator/resume";
    // @formatter:on

    @PostMapping(
            value = REGISTRATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Creates disabled user in the database and sends verification token to the provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Registration successfully done."),
            @ApiResponse(code = 400, message = "Values `password` and `match_password` do not match."),
            @ApiResponse(code = 409, message = "User already exists.")
    })
    ResponseEntity<RegistrationResponse> registration(@Valid @RequestBody RegistrationRequest request);

    @PostMapping(
            value = REGISTRATION_CONFIRM_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Accepts verification token provided by email and, if correct, enables user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Validation successful - user is now able to log in."),
            @ApiResponse(code = 404, message = "Provided token is either not found or expired.")
    })
    ResponseEntity<Void> confirmRegistration(@Valid @RequestBody TokenValidationRequest request);

    @ApiOperation(
            value = "Refreshes expired registration token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is refreshed and email has been sent with updated token value."),
            @ApiResponse(code = 404, message = "Provided token is not found.")
    })
    @GetMapping(value = REGISTRATION_TOKEN_REFRESH_ENDPOINT)
    ResponseEntity<Void> refreshRegistrationToken(@Valid RefreshRegistrationTokenRequest request);

    @PostMapping(
            value = REGISTRATION_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Resends email with existing token in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 404, message = "Could not find user associated with given email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found, it probably expired.")
    })
    ResponseEntity<Void> resendRegistrationToken(@Valid @RequestBody ResendTokenRequest request);

    @PostMapping(
            value = REGISTRATION_EVALUATOR_INVITATION,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'invite_evaluator')")
    @ApiOperation(value = "Sends an evaluator invitation to the provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation is sent successfully."),
            @ApiResponse(code = 409, message = "Evaluator with given email is already invited.")
    })
    ResponseEntity<Void> sendEvaluatorInvitation(@Valid @RequestBody EvaluatorInvitationRequest request);

    @GetMapping(value = REGISTRATION_EVALUATOR_INVITATION_CHECK)
    @ApiOperation(value = "Checks if evaluator is invited with given token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation exists."),
            @ApiResponse(code = 404, message = "Invitation is not found.")
    })
    ResponseEntity<Void> checkEvaluatorInvitation(@Valid @PathVariable("token") String token);

    @PostMapping(
            value = REGISTRATION_EVALUATOR_INVITATION_RESEND,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'invite_evaluator')")
    @ApiOperation(value = "Sends an evaluator invitation to the provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation is sent successfully."),
            @ApiResponse(code = 404, message = "Evaluator with given email is not invited.")
    })
    ResponseEntity<Void> resendEvaluatorInvitation(@Valid @RequestBody EvaluatorInvitationResendRequest request);

    @PostMapping(
            value = REGISTRATION_EVALUATOR_INVITATION_WITHDRAW,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'invite_evaluator')")
    @ApiOperation(value = "Withdraws an existing evaluator invitation.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation is withdrawn successfully."),
            @ApiResponse(code = 404, message = "Evaluator with given email is not invited.")
    })
    ResponseEntity<Void> withdrawEvaluatorInvitation(@Valid @RequestBody EvaluatorInvitationWithdrawalRequest request);

    @PostMapping(
            value = REGISTRATION_EVALUATOR_REQUEST,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'register_as_evaluator')")
    @ApiOperation(value = "Registers user as a pending evaluator in status REQUESTED.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invitation is sent successfully."),
            @ApiResponse(code = 409, message = "Evaluator with given email is already pending evaluator.")
    })
    ResponseEntity<Void> requestEvaluatorRegistration(@Valid EvaluatorRegistrationRequest request);

    @PostMapping(
            value = REGISTRATION_EVALUATOR_SUBMIT,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ApiOperation(value = "Creates new evaluator with given data. Evaluator must be invited first.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Evaluator is created successfully."),
            @ApiResponse(code = 404, message = "Evaluator with given email is not invited."),
            @ApiResponse(code = 404, message = "Specified country doesn't exist."),
            @ApiResponse(code = 409, message = "User with given email already exists.")
    })
    ResponseEntity<EvaluatorDto> submitEvaluator(@Valid SubmitEvaluatorRequest request);

    @PostMapping(
            value = REGISTRATION_ART_OWNER_SUBMIT,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ApiOperation(value = "Creates new art owner with given data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Art owner is created successfully."),
            @ApiResponse(code = 404, message = "Specified country doesn't exist."),
            @ApiResponse(code = 409, message = "User with given email already exists.")
    })
    ResponseEntity<ArtOwnerDto> submitArtOwner(@Valid SubmitArtOwnerRequest request);

    @GetMapping(value = REGISTRATION_EVALUATOR_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_users')")
    @ApiOperation(value = "Search pending evaluators.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of pending evaluators successfully retrieved.")
    })
    ResponseEntity<SearchPendingEvaluatorsResponse> searchPendingEvaluators(
            @Valid SearchPendingEvaluatorsRequest request,
            @Valid PageRequest page);

    @GetMapping(value = REGISTRATION_EVALUATOR_RESUME_DOWNLOAD)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'download_user_resume')")
    @ApiOperation(value = "Download pending evaluator cv.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "CV successfully downloaded."),
            @ApiResponse(code = 404, message = "Evaluator with given email is not pending."),
            @ApiResponse(code = 404, message = "Pending evaluator with given email doesn't have a resume."),
            @ApiResponse(code = 400, message = "Download resume failed for some reason.")
    })
    ResponseEntity<Resource> downloadEvaluatorResume(@Valid DownloadEvaluatorResumeRequest request);

}
