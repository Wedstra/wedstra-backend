package com.wedstra.app.wedstra.backend.Services;

import com.mongodb.client.result.UpdateResult;
import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.ServiceRepository;
import com.wedstra.app.wedstra.backend.Repo.VendorRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServiceServices {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    FileStore fileStore;

    public com.wedstra.app.wedstra.backend.Entity.Service getServiceById(String id) {
        Optional<com.wedstra.app.wedstra.backend.Entity.Service> service = serviceRepository.findById(id);
        return service.orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getAllServices() {
        return serviceRepository.findAll();
    }

//    public com.wedstra.app.wedstra.backend.Entity.Service createService(com.wedstra.app.wedstra.backend.Entity.Service service, String vendor_id) {
//
//        service.setVendor_id(vendor_id);
//        return serviceRepository.save(service);
//    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendor(String vendor_id) {
        Query query = new Query(Criteria.where("vendor_id").is(vendor_id));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public boolean deleteService(String id, String vendorId) {
        Query query = new Query(Criteria.where("id").is(id));
        com.wedstra.app.wedstra.backend.Entity.Service existingService = mongoTemplate.findOne(query, com.wedstra.app.wedstra.backend.Entity.Service.class);

        Vendor vendor = vendorRepository.findById(new ObjectId(vendorId))
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if(vendor == null){
            return false;
        }

        if (existingService != null) {
            if(vendor.getNoOfServices() > 0){
                vendor.setNoOfServices(vendor.getNoOfServices() - 1);
                serviceRepository.deleteById(id);
                vendorRepository.save(vendor);
                return true;
            }
            else {
                return false;
            }

        } else {
            return false;
        }

    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesBycategory(String category) {
        Query query = new Query(Criteria.where("category").is(category));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public com.wedstra.app.wedstra.backend.Entity.Service createService(
            String service_name, String description, String category,
            String minPrice, String maxPrice, String location,
            List<MultipartFile> files, String vendorId) throws IOException {

        try {
            // Convert vendorId (String) to ObjectId and fetch vendor
            Vendor vendor = vendorRepository.findById(new ObjectId(vendorId))
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // If vendor has "BASIC" plan and already added a service, prevent adding a new one
            if ("BASIC".equals(vendor.getPlanType()) && vendor.getNoOfServices() >= 1) {
                throw new RuntimeException("Basic plan allows only 1 service. Upgrade your plan.");
            }

            // Create new service entity
            LocalDateTime currentDateTime = LocalDateTime.now();
            com.wedstra.app.wedstra.backend.Entity.Service newService =
                    new com.wedstra.app.wedstra.backend.Entity.Service(
                            service_name, description, category, minPrice, maxPrice,
                            vendorId, location, currentDateTime, currentDateTime
                    );

            // Save service to database
            serviceRepository.save(newService);

            // Save images if files exist
            String serviceId = newService.getId().toString();
            Map<String, String> metadata = new HashMap<>();
            List<String> photoUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String photoUrl = fileStore.save(
                        file.getOriginalFilename(), file.getContentType(), vendorId,
                        Optional.of(metadata), file.getInputStream(),
                        generateKey(file, vendorId, serviceId)
                );
                photoUrls.add(photoUrl);
            }

            // Set images and save service again
            newService.setImages(photoUrls);
            serviceRepository.save(newService);

            // **Update vendor's noOfServices and save vendor**
            vendor.setNoOfServices(vendor.getNoOfServices() + 1);
            vendorRepository.save(vendor);  // Save the updated vendor object

            return newService;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateKey(MultipartFile file, String vendorId, String serviceId) {
        return vendorId + "/services/" + serviceId + "/" + file.getOriginalFilename();
    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByLocation(String location) {
        Query query = new Query(Criteria.where("location").is(location));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendorByLocation(String location, String vendorId) {
        Query query = new Query(Criteria.where("location").is(location).and("vendor_id").is(vendorId));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendorByCategory(String category, String vendorId) {
        Query query = new Query(Criteria.where("category").is(category).and("vendor_id").is(vendorId));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendorByLocationByCategory(
            String category, String location, String vendorId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("category").is(category)
                    .and("location").is(location)
                    .and("vendor_id").is(vendorId));

            return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching services by vendor, location, and category", e);
        }
    }
    public com.wedstra.app.wedstra.backend.Entity.Service updateService(String id, com.wedstra.app.wedstra.backend.Entity.Service serviceDetails) {
        // 1. Find the existing service by its ID or throw an exception
        com.wedstra.app.wedstra.backend.Entity.Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        // 2. Update fields only if they are present in the request body

        // Update String fields if not null
        if (serviceDetails.getService_name() != null) {
            existingService.setService_name(serviceDetails.getService_name());
        }
        if (serviceDetails.getDescription() != null) {
            existingService.setDescription(serviceDetails.getDescription());
        }
        if (serviceDetails.getCategory() != null) {
            existingService.setCategory(serviceDetails.getCategory());
        }
        if (serviceDetails.getMin_price() != null) {
            existingService.setMin_price(serviceDetails.getMin_price());
        }
        if (serviceDetails.getMax_price() != null) {
            existingService.setMax_price(serviceDetails.getMax_price());
        }
        if (serviceDetails.getVendor_id() != null) {
            existingService.setVendor_id(serviceDetails.getVendor_id());
        }
        if (serviceDetails.getLocation() != null) {
            existingService.setLocation(serviceDetails.getLocation());
        }

        // Update List fields if not null
        if (serviceDetails.getImages() != null) {
            existingService.setImages(serviceDetails.getImages());
        }
        if (serviceDetails.getReviews() != null) {
            existingService.setReviews(serviceDetails.getReviews());
        }

        // Update primitive double 'ratings' only if a non-default value is provided.
        // A primitive 'double' defaults to 0.0 if not in the JSON request body.
        // This check prevents an accidental update to 0.0.
        if (serviceDetails.getRatings() != 0.0) {
            existingService.setRatings(serviceDetails.getRatings());
        }

        // Always update the 'updated_at' timestamp to the current time on any update
        existingService.setUpdated_at(LocalDateTime.now());

        // 3. Save the updated entity back to the database and return it
        return serviceRepository.save(existingService);
    }

}
