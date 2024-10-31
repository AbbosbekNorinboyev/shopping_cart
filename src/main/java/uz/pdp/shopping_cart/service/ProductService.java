package uz.pdp.shopping_cart.service;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.shopping_cart.model.Product;

import java.util.List;

public interface ProductService {
    Product saveProduct(@NonNull Product product);

    List<Product> getAllProduct();
    Boolean deleteProduct(@NonNull Integer id);

    Product getProductById(@NonNull Integer id);
    Product updateProduct(Product product, MultipartFile file);
    List<Product> getAllActiveProduct(String category);
    List<Product> searchProduct(String ch);
    Page<Product> getAllActiveProductPagination(Integer page, Integer size, String category);
}
