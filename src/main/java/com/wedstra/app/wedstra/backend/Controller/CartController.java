package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.Cart;
import com.wedstra.app.wedstra.backend.Entity.CartItem;
import com.wedstra.app.wedstra.backend.Services.CartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartServices cartServices;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        try {
            Cart cart = cartServices.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/{userId}/addItem")
    public ResponseEntity<?> addItemToCart(
            @PathVariable String userId,
            @RequestBody CartItem cartItem,
            @RequestParam(defaultValue = "false") boolean forceReplace) {
        try {
            Cart updatedCart = cartServices.addItemToCart(userId, cartItem, forceReplace);
            return ResponseEntity.ok(updatedCart);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @DeleteMapping("/{cartId}/items/{serviceId}")
    public ResponseEntity<String> removeItem(
            @PathVariable String cartId,
            @PathVariable String serviceId) {
        cartServices.removeItemFromCart(cartId, serviceId);
        return ResponseEntity.ok("Item removed successfully");
    }
}
