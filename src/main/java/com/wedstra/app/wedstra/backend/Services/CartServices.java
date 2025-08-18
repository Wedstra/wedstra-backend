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
        //Cart is created or already exited logic
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


            //if cart is empty
            if(cart.getItems().isEmpty()){
//                simply add 1st item
                cart.getItems().add(newItem);
            }
            else {
                //else if cart already have items
                String vendor_lock_id = cart.getItems().get(0).getVendorId();
                //when already exited and new item have same vendor Id
                if(vendor_lock_id.equals(newItem.getVendorId())) {
                    cart.getItems().add(newItem);
                }
                else{
                    //if forceReplace is true
                    if(forceReplace){
                        //empty the cart items and add new item only and update total amount
                        cart.setItems(new ArrayList<>());
                        cart.setTotalAmount(0.0);

                        cart.getItems().add(newItem);
                    }
                    else{
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Cart already contains items from another vendor. Use forceReplace=true to override."
                        );
                    }
                    }
                }

            }
         catch (Exception e) {
            throw new RuntimeException(e);
        }
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
