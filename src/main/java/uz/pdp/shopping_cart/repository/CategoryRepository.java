package uz.pdp.shopping_cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.shopping_cart.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public Boolean existsByName(String name);

    List<Category> findByIsActiveTrue();
}