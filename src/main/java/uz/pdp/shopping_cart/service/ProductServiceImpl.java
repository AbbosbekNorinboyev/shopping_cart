package uz.pdp.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.shopping_cart.model.Product;
import uz.pdp.shopping_cart.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product saveProduct(@NonNull Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(@NonNull Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(@NonNull Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Override
    public Product updateProduct(Product updateProduct, MultipartFile file) {
        Product product = getProductById(updateProduct.getId());
        String imageName = file.isEmpty() ? product.getImage() : file.getOriginalFilename();
        product.setImage(imageName);
        product.setTitle(updateProduct.getTitle());
        product.setDescription(updateProduct.getDescription());
        product.setCategory(updateProduct.getCategory());
        product.setPrice(updateProduct.getPrice());
        product.setIsActive(updateProduct.getIsActive());
        product.setDiscount(updateProduct.getDiscount());
        double discount = product.getPrice() * (product.getDiscount() / 100.0);
        double discountPrice = product.getPrice() - discount;
        product.setDiscountPrice(discountPrice);
        product.setStock(updateProduct.getStock());
        Product savedProduct = productRepository.save(product);
        if (!ObjectUtils.isEmpty(savedProduct)) {
            if (!file.isEmpty()) {
                File saveFile = null;
                try {
                    saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
                            File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProduct(String category) {
        List<Product> products = null;
        if (ObjectUtils.isEmpty(category)) {
            products = productRepository.findByIsActiveTrue();
        } else {
            products = productRepository.findByCategory(category);
        }
        return products;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer page, Integer size, String category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> pageProducts = null;
        if (ObjectUtils.isEmpty(category)) {
            pageProducts = productRepository.findByIsActiveTrue(pageable);
        } else {
            pageProducts = productRepository.findByCategory(pageable, category);
        }
        return pageProducts;
    }
}
