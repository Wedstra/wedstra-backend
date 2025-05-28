package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.VendorRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
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

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public ResponseEntity<?> registerVendor(String username, String password, String vendorName, String businessName, String businessCategory, String email, String phoneNo, String city, String gstNumber, MultipartFile license, String termsAndConditions, MultipartFile vendorAadharCard, MultipartFile vendorPAN, MultipartFile businessPAN, MultipartFile electricityBill, List<MultipartFile> businessPhotos) throws IOException {
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
            photoUrls.add(url); // ✅ Directly use the returned URL
        }

        vendor.setVendor_aadharCard(fileUrls.get("vendor_aadharCard"));
        vendor.setVendor_PAN(fileUrls.get("vendor_PAN"));
        vendor.setBusiness_PAN(fileUrls.get("business_PAN"));
        vendor.setElectricity_bill(fileUrls.get("electricity_bill"));
        vendor.setLiscence(fileUrls.get("license"));
        vendor.setBusiness_photos(photoUrls);

        vendorRepository.save(vendor);

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

//    public String updateVendor(String id, Vendor vendor) {
//        Query query = new Query(Criteria.where("id").is(id));
//        Vendor existingVendor = mongoTemplate.findOne(query, Vendor.class);
//
//        if(existingVendor == null){
//            return "Vendor not found.";
//        }
//        else {
//            // Build the update object with the new values
//            Update update = new Update();
//            if (vendor.getVendor_name() != null) update.set("vendor_name", vendor.getVendor_name());
//            if (vendor.getBusiness_name() != null) update.set("business_name", vendor.getBusiness_name());
//            if (vendor.getBusiness_category() != null) update.set("business_category", vendor.getBusiness_category());
//            if (vendor.getVendor_aadharCard() != null) update.set("vendor_aadharCard", vendor.getVendor_aadharCard());
//            if (vendor.getVendor_PAN() != null) update.set("vendor_PAN", vendor.getVendor_PAN());
//            if (vendor.getBusiness_PAN() != null) update.set("business_PAN", vendor.getBusiness_PAN());
//            if (vendor.getElectricity_bill() != null) update.set("electricity_bill", vendor.getElectricity_bill());
//            if (vendor.getBusiness_photos() != null) update.set("business_photos", vendor.getBusiness_photos());
//            if (vendor.getLiscence() != null) update.set("liscence", vendor.getLiscence());
//            if (vendor.getGst_number() != null) update.set("gst_number", vendor.getGst_number());
//            if (vendor.getTerms_and_conditions() != null) update.set("terms_and_conditions", vendor.getTerms_and_conditions());
//            if (vendor.getEmail() != null) update.set("email", vendor.getEmail());
//            if (vendor.getPhone_no() != null) update.set("phone_no", vendor.getPhone_no());
//            if (vendor.getPassword() != null) update.set("password", vendor.getPassword());
//            if (vendor.getCity() != null) update.set("city", vendor.getCity());
//            if (vendor.getRole() != null) update.set("role", vendor.getRole());
//            if (vendor.getPlanType() != null) update.set("planType", vendor.getPlanType());
//            if (vendor.isVerified()) update.set("isAuthenticated", vendor.isVerified());
//
//            // Perform the update
//            mongoTemplate.updateFirst(query, update, Vendor.class);
//            return "vendor found.";
//        }
//    }

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

    public Vendor verifyVendor(String id) {
        Vendor existingVendor = vendorRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + id));

        if (!existingVendor.isVerified()) {
            existingVendor.setVerified(true);
            vendorRepository.save(existingVendor);
        }

        return existingVendor; // Return the vendor whether it was already verified or just updated
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


//    public String uploadImage(MultipartFile file) {
//        // save file to s3
//        //1. check if the file is empty
//        if(file.isEmpty()){
//            return "file is empty";
//        }
//        //2. check if the file is an image
//        if(!Arrays.asList("image/jpeg", "image/png", "image/gif").contains(file.getContentType())){
//            return "file is not an image";
//        }
//        //3. Grab the file metadata
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("Content-Type", file.getContentType());
//        metadata.put("Content-Length", String.valueOf(file.getSize()));
//        //4. Save the image to s3 and update the vendor image url
//
//        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), file.getOriginalFilename());
//        String fileName =  String.format("%s/%s", file.getName(), file.getOriginalFilename());
//        try {
//            fileStore.save(path, fileName, Optional.of(metadata), file.getInputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return "image uploaded";
//    }
}
