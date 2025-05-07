package com.wedstra.app.wedstra.backend.Services;



import com.wedstra.app.wedstra.backend.Entity.Review;
import com.wedstra.app.wedstra.backend.Repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByVendorId(String vendorId) {
        return reviewRepository.findByVendorId(vendorId);
    }

    public List<Review> getWebsiteReviews() {
        return reviewRepository.findByVendorIdIsNull();
    }

    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }
}

