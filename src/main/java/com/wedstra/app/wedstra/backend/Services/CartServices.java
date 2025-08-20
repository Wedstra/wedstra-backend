package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Cart;
import com.wedstra.app.wedstra.backend.Entity.CartItem;
import com.wedstra.app.wedstra.backend.Repo.CartItemRepository;
import com.wedstra.app.wedstra.backend.Repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CartServices {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart addItemToCart(String userId, CartItem newItem, boolean forceReplace) {
        // Find existing cart or create new one
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setItems(new ArrayList<>());
                    c.setTotalAmount(0.0);
                    return c;
                });

        // If cart empty → add first item
        if (cart.getItems().isEmpty()) {
            cart.getItems().add(newItem);
        } else {
            // Check vendor lock
            String vendorLockId = cart.getItems().get(0).getVendorId();

            if (vendorLockId.equals(newItem.getVendorId())) {
                // Same vendor → just add
                cart.getItems().add(newItem);
            } else {
                if (forceReplace) {
                    // Clear and replace with new vendor item
                    cart.setItems(new ArrayList<>());
                    cart.setTotalAmount(0.0);
                    cart.getItems().add(newItem);
                } else {
                    // Different vendor + no forceReplace → throw conflict
                    throw new IllegalArgumentException(
                            "Cart already contains items from another vendor. Use forceReplace=true to override."
                    );
                }
            }
        }

        // Update total amount
        cart.setTotalAmount(calculateTotal(cart.getItems()));

        return cartRepository.save(cart);
    }


    private double calculateTotal(List<CartItem> items) {
        return items.stream().mapToDouble(CartItem::getPrice).sum();
    }

    public Cart removeItemFromCart(String userId, String serviceId) {
        try {
            // Find the user's cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

            // Try to remove the item
            boolean removed = cart.getItems().removeIf(item -> Objects.equals(item.getServiceId(), serviceId));

            if (!removed) {
                throw new RuntimeException("Service not found in cart for deletion: " + serviceId);
            }

            // Recalculate total amount
            double total = cart.getItems().stream()
                    .mapToDouble(CartItem::getPrice)
                    .sum();
            cart.setTotalAmount(total);

            return cartRepository.save(cart);

        } catch (Exception e) {
            throw new RuntimeException("Failed to remove item from cart: " + e.getMessage(), e);
        }
    }


    public Cart getCartByUserId(String userId) {
        try {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving cart for userId: " + userId, e);
        }
    }


}
