package com.wedstra.app.wedstra.backend.Repo;


import com.wedstra.app.wedstra.backend.Entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByVendorId(String vendorId); // reviews for specific vendor
    List<Review> findByVendorIdIsNull(); // reviews for the entire website
}
