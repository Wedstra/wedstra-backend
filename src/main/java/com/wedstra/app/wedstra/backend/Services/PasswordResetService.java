package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.CustomException.PasswordResetException;
import com.wedstra.app.wedstra.backend.Entity.PasswordResetToken;
import com.wedstra.app.wedstra.backend.Entity.User;
import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.PasswordResetTokenRepository;
import com.wedstra.app.wedstra.backend.Repo.UserRepo;
import com.wedstra.app.wedstra.backend.Repo.VendorRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepo userRepo; // Your Mongo-based User repository

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VendorRepository vendorRepository;

    public void createResetToken(String email) throws MessagingException {
        Optional<User> userOpt = userRepo.findByEmail(email);
        Optional<Vendor> vendorOpt = vendorRepository.findByEmail(email);

        if (userOpt.isEmpty() && vendorOpt.isEmpty()) {
            throw new RuntimeException("User or Vendor not found");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserEmail(email);
        resetToken.setExpiryDate(expiry);
        tokenRepo.save(resetToken);

        if (userOpt.isPresent()) {
            String username = userOpt.get().getUsername();
            sendResetEmail(email, token, username);
        } else {
            String vendorName = vendorOpt.get().getVendor_name();
            sendResetEmail(email, token, vendorName);
        }
    }


    private void sendResetEmail(String email, String token, String username) throws MessagingException {
        String resetLink = "https://www.wedstra.com/reset-password?token=" + token;

        String htmlContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                            <h2 style="color: #333;">Password Reset Request</h2>
                            <p>Hello, <b>%s</b></p>
                            <p>We received a request to reset your password. Click the button below to set a new password:</p>
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">Reset Password</a>
                            </div>
                            <p>If the button above doesn’t work, copy and paste the following URL into your browser:</p>
                            <p style="word-break: break-all;"><a href="%s">%s</a></p>
                            <hr style="margin: 30px 0;" />
                            <p>This link will expire in 15 minutes for security reasons.</p>
                            <p>If you did not request a password reset, you can safely ignore this email.</p>
                            <p style="color: #777;">– The Wedstra Team</p>
                        </div>
                    </body>
                    </html>
                """.formatted(username,resetLink, resetLink, resetLink);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Reset Your Password - Wedstra");
        helper.setText(htmlContent, true); // true = isHtml

        mailSender.send(mimeMessage);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new PasswordResetException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new PasswordResetException("Token has expired. Please request a new reset link.");
        }

        String email = resetToken.getUserEmail();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Optional<User> userOpt = userRepo.findByEmail(email);
        Optional<Vendor> vendorOpt = vendorRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            user.setPasswordHash(encoder.encode(newPassword));
            userRepo.save(user);
        } else if (vendorOpt.isPresent()) {
            Vendor vendor = vendorOpt.get();
            vendor.setPassword(newPassword);
            vendor.setPasswordHash(encoder.encode(newPassword));
            vendorRepository.save(vendor);
        } else {
            throw new PasswordResetException("Associated user or vendor not found.");
        }

        tokenRepo.delete(resetToken);
    }


}

