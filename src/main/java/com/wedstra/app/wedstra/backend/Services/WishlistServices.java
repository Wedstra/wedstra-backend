package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.User;
import com.wedstra.app.wedstra.backend.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServices {
    @Autowired
    private UserRepo userRepository;

    public List<String> getWishlist(String userId) {
        User user = getUserOrThrow(userId);
        return user.getWishlistVendorIds();
    }

    public void addToWishlist(String userId, String vendorId) {
        try {
            User user = getUserOrThrow(userId);
            List<String> wishlist = user.getWishlistVendorIds();

            if (!wishlist.contains(vendorId)) {
                wishlist.add(vendorId);
                userRepository.save(user);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not add vendor '" + vendorId + "' to wishlist for user '" + userId + "'. Reason: " + e.getMessage());
            throw new RuntimeException("Failed to update wishlist. Please try again later.", e);
        }
    }

    public void removeFromWishlist(String userId, String vendorId) {
        User user = getUserOrThrow(userId);
        List<String> wishlist = user.getWishlistVendorIds();

        if (wishlist.remove(vendorId)) {
            userRepository.save(user);
        }
    }

    public boolean isInWishlist(String userId, String vendorId) {
        User user = getUserOrThrow(userId);
        return user.getWishlistVendorIds().contains(vendorId);
    }

    private User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
