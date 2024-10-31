package uz.pdp.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uz.pdp.shopping_cart.model.Category;
import uz.pdp.shopping_cart.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(@NonNull Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Boolean existCategory(@NonNull String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<Category> getCategoryAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Boolean deleteCategory(@NonNull Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        if (!ObjectUtils.isEmpty(category)) {
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(@NonNull Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Override
    public List<Category> getAllActiveCategory() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    public Page<Category> getAllCategoryPagination(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable);
    }
}
