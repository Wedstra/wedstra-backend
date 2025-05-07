package com.wedstra.app.wedstra.backend.Controller;


import com.wedstra.app.wedstra.backend.Entity.Review;
import com.wedstra.app.wedstra.backend.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/vendor/{vendorId}")
    public List<Review> getVendorReviews(@PathVariable String vendorId) {
        return reviewService.getReviewsByVendorId(vendorId);
    }

    @GetMapping("/website")
    public List<Review> getWebsiteReviews() {
        return reviewService.getWebsiteReviews();
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
    }
}

