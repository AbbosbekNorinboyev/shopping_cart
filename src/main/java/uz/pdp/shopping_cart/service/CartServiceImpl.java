package uz.pdp.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uz.pdp.shopping_cart.model.Cart;
import uz.pdp.shopping_cart.model.Product;
import uz.pdp.shopping_cart.model.Users;
import uz.pdp.shopping_cart.repository.CartRepository;
import uz.pdp.shopping_cart.repository.ProductRepository;
import uz.pdp.shopping_cart.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart saveCart(@NonNull Integer productId, @NonNull Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        Cart cartStatus = cartRepository.findCartByProductIdAndUserId(productId, userId);
        Cart cart = null;
        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getAllCart(@NonNull Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        Double totalOrderPrice = 0.0;
        ArrayList<Cart> updateCarts = new ArrayList<>();
        for (Cart cart : carts) {
            Double totalPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(cart);
        }
        return carts;
    }

    @Override
    public Integer getCountCart(@NonNull Integer userId) {
        return cartRepository.countByUserId(userId);
    }

    @Override
    public void updateCartQuantity(@NonNull String sy, @NonNull Integer cartId) {
        Cart cart = cartRepository.findById(cartId).get();
        int updateQuantity = 0;
        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;
            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }
    }
}
