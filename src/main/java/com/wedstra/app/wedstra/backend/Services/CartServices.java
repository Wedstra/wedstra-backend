package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Cart;
import com.wedstra.app.wedstra.backend.Entity.CartItem;
import com.wedstra.app.wedstra.backend.Repo.CartItemRepository;
import com.wedstra.app.wedstra.backend.Repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class CartServices {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

//    public Cart addItemToCart(String userId, CartItem newItem, boolean forceReplace) {
//        // Find existing cart or create new one
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseGet(() -> {
//                    Cart c = new Cart();
//                    c.setUserId(userId);
//                    c.setItems(new ArrayList<>());
//                    c.setTotalAmount(0.0);
//                    return c;
//                });
//
//        // If cart empty → add first item
//        if (cart.getItems().isEmpty()) {
//            cart.getItems().add(newItem);
//        } else {
//            // Check vendor lock
//            String vendorLockId = cart.getItems().get(0).getVendorId();
//
//            if (vendorLockId.equals(newItem.getVendorId())) {
//                // Same vendor → just add
//                cart.getItems().add(newItem);
//            } else {
//                if (forceReplace) {
//                    // Clear and replace with new vendor item
//                    cart.setItems(new ArrayList<>());
//                    cart.setTotalAmount(0.0);
//                    cart.getItems().add(newItem);
//                } else {
//                    // Different vendor + no forceReplace → throw conflict
//                    throw new IllegalArgumentException(
//                            "Cart already contains items from another vendor. Use forceReplace=true to override."
//                    );
//                }
//            }
//        }
//
//        // Update total amount
//        cart.setTotalAmount(calculateTotal(cart.getItems()));
//
//        return cartRepository.save(cart);
//    }

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

        if (cart.getItems().isEmpty()) {
            // Cart empty → add first item
            cart.getItems().add(newItem);
        } else {
            // Vendor lock check
            String vendorLockId = cart.getItems().get(0).getVendorId();

            if (vendorLockId.equals(newItem.getVendorId())) {
                // Same vendor → check if service already exists
                Optional<CartItem> existingItemOpt = cart.getItems().stream()
                        .filter(item -> item.getServiceId().equals(newItem.getServiceId()))
                        .findFirst();

                if (existingItemOpt.isPresent()) {
                    // Service already exists → increase quantity
                    CartItem existingItem = existingItemOpt.get();
                    existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                } else {
                    // New service → add to cart
                    cart.getItems().add(newItem);
                }
            } else {
                if (forceReplace) {
                    // Replace vendor’s items with new one
                    cart.setItems(new ArrayList<>());
                    cart.setTotalAmount(0.0);
                    cart.getItems().add(newItem);
                } else {
                    throw new IllegalArgumentException(
                            "Cart already contains items from another vendor. Use forceReplace=true to override."
                    );
                }
            }
        }

        // ✅ Always recalc total using price × quantity
        cart.setTotalAmount(calculateTotal(cart.getItems()));

        return cartRepository.save(cart);
    }





    private double calculateTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }


    public void removeItemFromCart(String cartId, String serviceId) {
        // Fetch cart
        Cart cart = mongoTemplate.findById(cartId, Cart.class);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with id: " + cartId);
        }

        // Find the item to remove
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getServiceId().equals(serviceId))
                .findFirst()
                .orElse(null);

        if (itemToRemove == null) {
            throw new IllegalArgumentException("Service not found in cart: " + serviceId);
        }

        // Subtract price from total
        double pricePerUnit = itemToRemove.getPrice();

        if (itemToRemove.getQuantity() > 1) {
            // Reduce quantity by 1
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
            cart.setTotalAmount(cart.getTotalAmount() - pricePerUnit);
        } else {
            // Quantity is 1 → remove item completely
            cart.setTotalAmount(cart.getTotalAmount() - pricePerUnit);
            cart.getItems().remove(itemToRemove);
        }

        // Ensure total is never negative
        if (cart.getTotalAmount() < 0) {
            cart.setTotalAmount(0);
        }

        // Save updated cart
        mongoTemplate.save(cart);
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
