package uz.pdp.shopping_cart.service;

import org.springframework.lang.NonNull;
import uz.pdp.shopping_cart.model.Cart;

import java.util.List;

public interface CartService {
    Cart saveCart(@NonNull Integer productId, @NonNull Integer userId);
    List<Cart>  getAllCart(@NonNull Integer userId);
    Integer getCountCart(@NonNull Integer userId);
    void updateCartQuantity(@NonNull String sy, @NonNull Integer cartId);
}
