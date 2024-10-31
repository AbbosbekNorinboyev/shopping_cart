package uz.pdp.shopping_cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.shopping_cart.model.ProductOrder;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(Integer userId);

    ProductOrder findByOrderId(String orderId);
}