package uz.pdp.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import uz.pdp.shopping_cart.model.Cart;
import uz.pdp.shopping_cart.model.OrderAddress;
import uz.pdp.shopping_cart.model.OrderRequest;
import uz.pdp.shopping_cart.model.ProductOrder;
import uz.pdp.shopping_cart.repository.CartRepository;
import uz.pdp.shopping_cart.repository.ProductOrderRepository;
import uz.pdp.shopping_cart.util.CommonUtil;
import uz.pdp.shopping_cart.util.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductOrderRepository productOrderRepository;
    private final CartRepository cartRepository;
    private final CommonUtil commonUtil;

    @Override
    public void saveProductOrder(@NonNull Integer userId, OrderRequest orderRequest) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDateTime.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNumber(orderRequest.getMobileNumber());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            ProductOrder savedProductOrder = productOrderRepository.save(order);
            commonUtil.sendMailForProductOrder(savedProductOrder, "success");
        }
    }

    @Override
    public List<ProductOrder> getAllProductOrderByUser(@NonNull Integer userId) {
        return productOrderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findById(id);
        if (productOrderOptional.isPresent()) {
            ProductOrder productOrder = productOrderOptional.get();
            productOrder.setStatus(status);
            return productOrderRepository.save(productOrder);
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllProductOrder() {
        return productOrderRepository.findAll();
    }

    @Override
    public ProductOrder getProductOrderByOrderId(String orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }
}
