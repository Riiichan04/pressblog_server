package vn.id.devblog.blog_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.common.enums.VerificationType;
import vn.id.devblog.blog_server.dto.request.verify.VerifyRequest;
import vn.id.devblog.blog_server.dto.response.verify.GenerateVerifyResponse;
import vn.id.devblog.blog_server.services.VerificationService;

@RestController
@RequestMapping("/verify")
public class VerificationController {
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send/reset-password")
    public ResponseEntity<GenerateVerifyResponse> sendResetPassword(@RequestBody String email) {
        //TODO: Fix email format here
        String cleanEmail = email.replaceAll("^\"|\"$", "");
        GenerateVerifyResponse response = verificationService.sendVerificationCode(cleanEmail, VerificationType.RESET_PASSWORD);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send/account")
    public ResponseEntity<GenerateVerifyResponse> sendVerifyAccount(@RequestBody String email) {
        //TODO: Fix email format here
        String cleanEmail = email.replaceAll("^\"|\"$", "");
        GenerateVerifyResponse response = verificationService.sendVerificationCode(cleanEmail, VerificationType.VERIFY_USER);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenerateVerifyResponse> resetPassword(@RequestBody VerifyRequest request) {
        GenerateVerifyResponse response = verificationService.verifyResetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/account")
    public ResponseEntity<GenerateVerifyResponse> verifyAccount(@RequestBody VerifyRequest request) {
        GenerateVerifyResponse response = verificationService.verifyUser(request.email(), request.code());
        return ResponseEntity.ok(response);
    }
}
