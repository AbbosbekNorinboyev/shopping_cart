package uz.pdp.shopping_cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import uz.pdp.shopping_cart.model.Cart;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findCartByProductIdAndUserId(Integer productId, Integer userId);

    Integer countByUserId(@NonNull Integer userId);

    List<Cart> findByUserId(Integer userId);
}