package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Cart;
import com.wedstra.app.wedstra.backend.Entity.CartItem;
import com.wedstra.app.wedstra.backend.Repo.CartItemRepository;
import com.wedstra.app.wedstra.backend.Repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CartServices {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart addItemToCart(String userId, CartItem newItem) {
        Cart cart = null;
        try {
            // Find existing cart for the user or create a new one
            cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart c = new Cart();
                        c.setUserId(userId);
                        c.setItems(new ArrayList<>());
                        c.setTotalAmount(0.0);
                        return c;
                    });

            System.out.println("Cart  = "+cart.getUserId());
            System.out.println("Cart  = "+cart.getItems());
            System.out.println("Cart  = "+cart.getTotalAmount());


            AtomicBoolean isServiceAlreadyExists = new AtomicBoolean(false);
            cart.getItems().forEach((item)->{
                if(Objects.equals(item.getServiceId(), newItem.getServiceId())){
                    isServiceAlreadyExists.set(true);
                }
            });


            if(!isServiceAlreadyExists.get()){
                //add to cart
                cart.getItems().add(newItem);
                System.out.println("Service added to cart");
            }else {
                //return already exits
                System.out.println("Service already exists cart");
            }

            // Update total cart amount
            double total = cart.getItems().stream()
                    .mapToDouble(CartItem::getPrice)
                    .sum();
            cart.setTotalAmount(total);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return cartRepository.save(cart);
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
