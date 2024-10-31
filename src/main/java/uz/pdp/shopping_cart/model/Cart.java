package uz.pdp.shopping_cart.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Users user;
    @ManyToOne
    private Product product;
    private Integer quantity;
    @Transient
    private Double totalPrice;
    @Transient
    private Double totalOrderPrice;
}
