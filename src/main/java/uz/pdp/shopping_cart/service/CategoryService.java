package uz.pdp.shopping_cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import uz.pdp.shopping_cart.model.Category;

import java.util.List;

public interface CategoryService {
    Category saveCategory(@NonNull Category category);
    Boolean existCategory(@NonNull String name);
    List<Category> getCategoryAll();
    Boolean deleteCategory(@NonNull Integer id);
    Category getCategoryById(@NonNull Integer id);
    List<Category> getAllActiveCategory();
    Page<Category> getAllCategoryPagination(Integer page, Integer size);
}
