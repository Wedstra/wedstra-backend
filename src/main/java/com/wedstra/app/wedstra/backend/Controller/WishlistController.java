package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Services.WishlistServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    private WishlistServices wishlistService;

    // Get wishlist vendor IDs
    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getWishlist(@PathVariable String userId) {
        List<String> wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    // Add vendor to wishlist
    @PostMapping("/{userId}/add")
    public ResponseEntity<String> addToWishlist(
            @PathVariable String userId,
            @RequestParam String vendorId) {
        try {
            wishlistService.addToWishlist(userId, vendorId);
            return ResponseEntity.ok("Vendor added to wishlist");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add vendor to wishlist: " + e.getMessage());
        }
    }

    // Remove vendor from wishlist
        @DeleteMapping("/{userId}/remove")
    public ResponseEntity<String> removeFromWishlist(
            @PathVariable String userId,
            @RequestParam String vendorId) {
        wishlistService.removeFromWishlist(userId, vendorId);
        return ResponseEntity.ok("Vendor removed from wishlist");
    }

    // Check if vendor is in wishlist
    @GetMapping("/{userId}/contains")
    public ResponseEntity<Boolean> isInWishlist(
            @PathVariable String userId,
            @RequestParam String vendorId) {
        boolean exists = wishlistService.isInWishlist(userId, vendorId);
        return ResponseEntity.ok(exists);
    }
}
