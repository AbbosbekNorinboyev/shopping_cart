package uz.pdp.shopping_cart.service;

import org.springframework.lang.NonNull;
import uz.pdp.shopping_cart.model.OrderRequest;
import uz.pdp.shopping_cart.model.ProductOrder;

import java.util.List;

public interface OrderService {
    void saveProductOrder(@NonNull Integer userId, OrderRequest orderRequest);
    List<ProductOrder> getAllProductOrderByUser(@NonNull Integer userId);
    ProductOrder updateOrderStatus(Integer id, String status);
    List<ProductOrder> getAllProductOrder();
    ProductOrder getProductOrderByOrderId(String orderId);

}
