package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Repo.ServiceRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    FileStore fileStore;

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getAllServices(){
        return serviceRepository.findAll();
    }

//    public com.wedstra.app.wedstra.backend.Entity.Service createService(com.wedstra.app.wedstra.backend.Entity.Service service, String vendor_id) {
//
//        service.setVendor_id(vendor_id);
//        return serviceRepository.save(service);
//    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendor(String vendor_id){
        Query query = new Query(Criteria.where("vendor_id").is(vendor_id));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public boolean deleteService(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        com.wedstra.app.wedstra.backend.Entity.Service existingService = mongoTemplate.findOne(query, com.wedstra.app.wedstra.backend.Entity.Service.class);

        if(existingService != null){
            serviceRepository.deleteById(id);
            return true;
        }else{
            return false;
        }

    }

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesBycategory(String category) {
        Query query = new Query(Criteria.where("category").is(category));
        return mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
    }

    public com.wedstra.app.wedstra.backend.Entity.Service createService(String serviceName, String description, String category, String minPrice, String maxPrice, String location, List<MultipartFile> files, String vendorId) throws IOException {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            com.wedstra.app.wedstra.backend.Entity.Service newService = new com.wedstra.app.wedstra.backend.Entity.Service(serviceName,description,category,minPrice,maxPrice,vendorId,location,currentDateTime,currentDateTime);

            serviceRepository.save(newService);

            String service_id = newService.getId().toString();

            Map<String, String> fileUrls = new HashMap<>();
            Map<String, String> metadata = new HashMap<>();

            List<String> photoUrls = new ArrayList<>();
            for(MultipartFile file: files){
                String photoUrl = fileStore.save(file.getOriginalFilename(),file.getContentType(), vendorId , Optional.of(metadata),file.getInputStream(), generateKey(file, vendorId, service_id));
                fileUrls.put("images", photoUrl);
                photoUrls.add(photoUrl);
            }
            newService.setImages(photoUrls);
            serviceRepository.save(newService);
            return newService;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateKey(MultipartFile file, String vendorId, String serviceId) {
        return vendorId + "/services/" + serviceId + "/"+file.getOriginalFilename();
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

    public List<com.wedstra.app.wedstra.backend.Entity.Service> getServicesByVendorByLocationByCategory(String category, String location, String vendorId) {
        List<com.wedstra.app.wedstra.backend.Entity.Service> services = null;
        try {
            Query query = new Query(Criteria.where("category").is(category).and("location").is(location).and("vendor_id").is(vendorId));
            services = mongoTemplate.find(query, com.wedstra.app.wedstra.backend.Entity.Service.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return services;
    }
}
