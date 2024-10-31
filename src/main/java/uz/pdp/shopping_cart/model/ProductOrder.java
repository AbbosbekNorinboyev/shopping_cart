package uz.pdp.shopping_cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderId;
    private LocalDateTime orderDate;
    @ManyToOne
    private Product product;
    private Double price;
    private Integer quantity;
    @ManyToOne
    private Users user;
    private String status;
    private String paymentType;
    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
}
