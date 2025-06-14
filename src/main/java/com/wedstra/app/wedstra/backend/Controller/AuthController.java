package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.CustomException.PasswordResetException;
import com.wedstra.app.wedstra.backend.Services.PasswordResetService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) throws MessagingException {
        resetService.createResetToken(request.get("email"));
        return ResponseEntity.ok("Reset link sent to email");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            resetService.resetPassword(request.get("token"), request.get("newPassword"));
            return ResponseEntity.ok("✅ Password reset successful!");
        } catch (PasswordResetException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Something went wrong. Please try again later.");
        }
    }

}

