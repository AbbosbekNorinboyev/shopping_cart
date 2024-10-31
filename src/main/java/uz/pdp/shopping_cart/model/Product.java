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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 500)
    private String title;
    @Column(length = 5000)
    private String description;
    private String category;
    private Double price;
    private Integer stock;
    private String image;
    private Integer discount;
    private Double discountPrice;
    private Boolean isActive;
}
