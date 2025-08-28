package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends MongoRepository<Service, String>{
        List<Service> getServicesById(String vendor_id);
//        List<Service> findByVendorId(String vendor_id);
        void deleteById(String id);
}
