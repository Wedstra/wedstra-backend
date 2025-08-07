package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.VendorRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class VendorServices {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private FileStore fileStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public ResponseEntity<?> registerVendor(String username, String password, String vendorName, String businessName, String businessCategory, String email, String phoneNo, String city, String gstNumber, MultipartFile license, String termsAndConditions, MultipartFile vendorAadharCard, MultipartFile vendorPAN, MultipartFile businessPAN, MultipartFile electricityBill, List<MultipartFile> businessPhotos) throws IOException, MessagingException {
        Vendor vendor = new Vendor(username, password, vendorName, businessName, businessCategory, email, phoneNo, city, gstNumber, termsAndConditions);
        vendor.setPasswordHash(passwordEncoder.encode(password));
        vendor.setRole("VENDOR");
        vendor.setNoOfServices(0);
        vendor.setPlanType("BASIC");
        vendor.setVerified(false);
        vendorRepository.save(vendor);

        //step 2 Save the images to the S3 bucket
        String vendorId = vendor.getId();
        Map<String, String> fileUrls = new HashMap<>();

        Map<String, String> metadata = new HashMap<>();

        fileUrls.put("vendor_aadharCard", fileStore.save(vendorAadharCard.getOriginalFilename(), "vendor_adhaarCard", vendorId, Optional.of(metadata), vendorAadharCard.getInputStream(), generateKey(vendorAadharCard, vendorId, "adhaarCard")));
        fileUrls.put("vendor_PAN", fileStore.save(vendorPAN.getOriginalFilename(), "vendor_PAN", vendorId, Optional.of(metadata), vendorPAN.getInputStream(), generateKey(vendorAadharCard, vendorId, "PAN Card")));
        fileUrls.put("business_PAN", fileStore.save(businessPAN.getOriginalFilename(), "business_PAN", vendorId, Optional.of(metadata), businessPAN.getInputStream(), generateKey(vendorAadharCard, vendorId, "Business PAN")));
        fileUrls.put("electricity_bill", fileStore.save(electricityBill.getOriginalFilename(), "electricity_bill", vendorId, Optional.of(metadata), electricityBill.getInputStream(), generateKey(vendorAadharCard, vendorId, "Electricity Bill")));
        fileUrls.put("license", fileStore.save(license.getOriginalFilename(), "license", vendorId, Optional.of(metadata), license.getInputStream(), generateKey(vendorAadharCard, vendorId, "license")));


        // Step 3: Upload business photos (bug fixed here)
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile businessPhoto : businessPhotos) {
            String url = fileStore.save(
                    businessPhoto.getOriginalFilename(),
                    "business_photos",
                    vendorId,
                    Optional.of(metadata),
                    businessPhoto.getInputStream(),
                    generateKey(businessPhoto, vendorId, "business_photos"));
            photoUrls.add(url); // Directly use the returned URL
        }

        vendor.setVendor_aadharCard(fileUrls.get("vendor_aadharCard"));
        vendor.setVendor_PAN(fileUrls.get("vendor_PAN"));
        vendor.setBusiness_PAN(fileUrls.get("business_PAN"));
        vendor.setElectricity_bill(fileUrls.get("electricity_bill"));
        vendor.setLiscence(fileUrls.get("license"));
        vendor.setBusiness_photos(photoUrls);

        vendorRepository.save(vendor);


        //send register success email
//        String loginLink = "http://localhost:3000/vendor-login";
        String loginLink = "https://www.wedstra.com/vendor-login";

        String subject = "Welcome to Wedstra! Your Vendor Registration is Successful.";
        String htmlContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                            <h2 style="color: #28a745;">🎉 Vendor Registration Successful</h2>
                            <p>Dear Vendor,</p>
                            <p><b>%s</b></p>
                            <p>Thank you for registering with <strong>Wedstra</strong>.</p>
                            <p>Your registration has been successfully received. Our team is currently reviewing your submitted documents. This process typically takes <strong>2–4 working days</strong>.</p>
                
                            <p>Once your documents are verified and approved, your vendor account will be activated. You will then be able to access your dashboard and start offering your services.</p>
                
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">Go to Login</a>
                            </div>
                
                            <p>If the button above doesn’t work, copy and paste the following URL into your browser:</p>
                            <p style="word-break: break-all;"><a href="%s">%s</a></p>
                
                            <hr style="margin: 30px 0;" />
                            <p>We appreciate your patience during this verification period. If you have any questions, feel free to contact our support team at <a href="mailto:support@wedstra.com">support@wedstra.com</a>.</p>
                            <p style="color: #777;">– The Wedstra Team</p>
                        </div>
                    </body>
                    </html>
                """.formatted(vendor.getVendor_name(),loginLink, loginLink, loginLink);

        sendMail(vendor.getEmail(), subject, htmlContent);

        return ResponseEntity.ok(vendor);
    }

    private String generateKey(MultipartFile vendorAadharCard, String vendorId, String adhaarCard) {
        String key = vendorId + "/documents/" + "/" + adhaarCard + "_" + vendorAadharCard.getOriginalFilename();
        return key;
    }


    public String deleteVendor(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        Vendor existingVendor = mongoTemplate.findOne(query, Vendor.class);

        if (existingVendor == null) {
            return "Vendor not found.";
        } else {
            vendorRepository.deleteById(id);
            return "vendor deleted with ID" + id;
        }
    }


    public String updateVendor(String id, Vendor vendor) {
        Query query = new Query(Criteria.where("id").is(id));
        Vendor existingVendor = mongoTemplate.findOne(query, Vendor.class);

        if (existingVendor == null) {
            return "Vendor not found.";
        } else {
            // Build the update object with the new values
            Update update = new Update();
            if (vendor.getVendor_name() != null) update.set("vendor_name", vendor.getVendor_name());
            if (vendor.getBusiness_name() != null) update.set("business_name", vendor.getBusiness_name());
            if (vendor.getBusiness_category() != null) update.set("business_category", vendor.getBusiness_category());
            if (vendor.getVendor_aadharCard() != null) update.set("vendor_aadharCard", vendor.getVendor_aadharCard());
            if (vendor.getVendor_PAN() != null) update.set("vendor_PAN", vendor.getVendor_PAN());
            if (vendor.getBusiness_PAN() != null) update.set("business_PAN", vendor.getBusiness_PAN());
            if (vendor.getElectricity_bill() != null) update.set("electricity_bill", vendor.getElectricity_bill());
            if (vendor.getBusiness_photos() != null) update.set("business_photos", vendor.getBusiness_photos());
            if (vendor.getLiscence() != null) update.set("liscence", vendor.getLiscence());
            if (vendor.getGst_number() != null) update.set("gst_number", vendor.getGst_number());
            if (vendor.getTerms_and_conditions() != null)
                update.set("terms_and_conditions", vendor.getTerms_and_conditions());
            if (vendor.getEmail() != null) update.set("email", vendor.getEmail());
            if (vendor.getPhone_no() != null) update.set("phone_no", vendor.getPhone_no());
            if (vendor.getPassword() != null) update.set("password", vendor.getPassword());
            if (vendor.getCity() != null) update.set("city", vendor.getCity());
            if (vendor.getRole() != null) update.set("role", vendor.getRole());
            if (vendor.getPlanType() != null) update.set("planType", vendor.getPlanType());
            if (vendor.isVerified()) update.set("isVerified", vendor.isVerified());

            // Perform the update
            mongoTemplate.updateFirst(query, update, Vendor.class);
            return "Vendor updated successfully.";
        }
    }


    public String authenticate(String username, String password) {
        Vendor vendor = vendorRepository.findByUsername(username);
        if (vendor != null) {
            if (vendor.getPassword().equals(password)) {
                return jwtServices.generateToken(username, vendor.getId());
            } else {
                return "bad credentials";
            }
        } else {
            return "vendor not found";
        }
    }

    public Vendor getVendorByUserName(String username) {
        return vendorRepository.findByUsername(username);
    }

    public List<Vendor> getVendorByLocationByCategory(String category, String location) {
        Query query = new Query(Criteria.where("business_category").is(category).and("city").is(location).and("isVerified").is(true));
        return mongoTemplate.find(query, Vendor.class);
    }

    public Vendor getVendorById(String id) {
        return vendorRepository.findById(new ObjectId(id)).orElse(null);
    }

    public Vendor verifyVendor(String id) throws MessagingException {
        Optional<Vendor> vendorOptional = vendorRepository.findById(new ObjectId(id));

        if (vendorOptional.isEmpty()) {
            throw new RuntimeException("Vendor not found with ID: " + id);
        }

        Vendor existingVendor = vendorOptional.get();
        if (!existingVendor.isVerified()) {
            existingVendor.setVerified(true);
            vendorRepository.save(existingVendor);
        }

        //send register success email
//        String loginLink = "http://localhost:3000/vendor-login";
        String loginLink = "https://www.wedstra.com/vendor-login";
        String subject = "Your Wedstra Vendor Profile Has Been Verified!";
        String htmlContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                            <h2 style="color: #28a745;">✅ Vendor Verification Complete</h2>
                            <p>Dear Vendor,</p>
                            <p><b>%s</b></p>
                            <p>We’re excited to inform you that your vendor profile on <strong>Wedstra</strong> has been <strong>successfully verified</strong>.</p>
                
                            <p>You now have full access to your vendor dashboard. You can log in to start managing your services, responding to inquiries, and growing your presence on our platform.</p>
                
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">Go to Dashboard</a>
                            </div>
                
                            <p>If the button above doesn’t work, copy and paste the following URL into your browser:</p>
                            <p style="word-break: break-all;"><a href="%s">%s</a></p>
                
                            <hr style="margin: 30px 0;" />
                            <p>If you have any questions or need assistance, feel free to contact us at <a href="mailto:support@wedstra.com">support@wedstra.com</a>.</p>
                            <p style="color: #777;">– The Wedstra Team</p>
                        </div>
                    </body>
                    </html>
                """.formatted(existingVendor.getVendor_name(),loginLink, loginLink, loginLink);


        sendMail(existingVendor.getEmail(), subject, htmlContent);

        return existingVendor;
    }


    public List<Vendor> getVerifiedVendors() {
        Query query = new Query(Criteria.where("isVerified").is(true));
        return mongoTemplate.find(query, Vendor.class);
    }

    public List<Vendor> getNotVerifiedVendors() {
        Query query = new Query(Criteria.where("isVerified").is(false));
        return mongoTemplate.find(query, Vendor.class);
    }

    public List<Vendor> getVendorByCategory(String category) {
        Query query = new Query(Criteria.where("business_category").is(category).and("isVerified").is(true));
        return mongoTemplate.find(query, Vendor.class);
    }


    private void sendMail(String email, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(mimeMessage);
    }

    public List<Vendor> findByIdIn(List<String> ids) {
        List<Vendor> vendors = vendorRepository.findByIdIn(ids);
        return vendors;
    }
}
