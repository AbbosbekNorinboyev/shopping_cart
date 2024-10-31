package uz.pdp.shopping_cart.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.shopping_cart.model.Category;
import uz.pdp.shopping_cart.model.Product;
import uz.pdp.shopping_cart.model.ProductOrder;
import uz.pdp.shopping_cart.model.Users;
import uz.pdp.shopping_cart.repository.CategoryRepository;
import uz.pdp.shopping_cart.service.*;
import uz.pdp.shopping_cart.util.CommonUtil;
import uz.pdp.shopping_cart.util.OrderStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CommonUtil commonUtil;

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            Users user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            Integer countCart = cartService.getCountCart(user.getId());
            model.addAttribute("countCart", countCart);
        }
        List<Category> allActiveCategories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", allActiveCategories);
    }

    @GetMapping("/")
    public String index() {
        return "admin/indexes";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model model,
                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
//        model.addAttribute("categories", categoryService.getCategoryAll());
        Page<Category> categories = categoryService.getAllCategoryPagination(page, size);
        model.addAttribute("categories", categories.getContent());
        model.addAttribute("productSize", categories.getSize());
        model.addAttribute("pageNumber", categories.getNumber());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalElements", categories.getTotalElements());
        model.addAttribute("totalPage", categories.getTotalPages());
        model.addAttribute("isFirst", categories.isFirst());
        model.addAttribute("isLast", categories.isLast());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) throws IOException {
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);
        Boolean existCategory = categoryService.existCategory(category.getName());
        if (existCategory) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category name already exists");
        } else {
            Category savedCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(savedCategory)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Category not saved! Internal server error");
            } else {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
                        File.separator + file.getOriginalFilename());
                System.out.println("path: " + path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                redirectAttributes.addFlashAttribute("successMessage", "Saved successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        Boolean deletedCategory = categoryService.deleteCategory(id);
        if (deletedCategory) {
            session.setAttribute("successMsg", "Category successfully deleted");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable Integer id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category updateCategory,
                                 @RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) throws IOException {
        Category category = categoryService.getCategoryById(updateCategory.getId());
        String imageName = file.isEmpty() ? category.getImageName() : file.getOriginalFilename();
        if (!ObjectUtils.isEmpty(updateCategory)) {
            category.setName(updateCategory.getName());
            category.setIsActive(updateCategory.getIsActive());
            category.setImageName(imageName);
        }
        Category savedCategory = categoryService.saveCategory(category);
        if (!ObjectUtils.isEmpty(savedCategory)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
                        File.separator + file.getOriginalFilename());
                System.out.println("path: " + path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                redirectAttributes.addFlashAttribute("successMessage", "Saved successfully");
            }
            redirectAttributes.addFlashAttribute("successMessage", "Successfully updated");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong on server");
        }
        return "redirect:/admin/loadEditCategory/" + updateCategory.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                              RedirectAttributes redirectAttributes) throws IOException {
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product savedProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(savedProduct)) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" +
                    File.separator + image.getOriginalFilename());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            redirectAttributes.addFlashAttribute("successMessage", "Saved successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Product successfully saved");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong on server");
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/loadViewProduct")
    public String loadViewProduct(Model model, @RequestParam(defaultValue = "") String ch) {
        List<Product> products = null;
        if (ch != null && ch.length() > 0) {
            products = productService.searchProduct(ch);
        } else {
            products = productService.getAllProduct();
        }
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes session) {
        Boolean deletedProduct = productService.deleteProduct(id);
        if (deletedProduct) {
            session.addFlashAttribute("successMsg", "Product successfully deleted");
        } else {
            session.addFlashAttribute("errorMsg", "Something wrong on server");
        }
        return "admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product updateProduct,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        if (updateProduct.getDiscount() < 0 || updateProduct.getDiscount() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid input");
        } else {
            try {
                productService.updateProduct(updateProduct, file);
                redirectAttributes.addFlashAttribute("successMessage", "Product successfully updated");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong on the server: " + e.getMessage());
            }
        }
        return "redirect:/admin/editProduct/" + updateProduct.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<Users> users = userService.getUsers("ROLE_USER");
        model.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateUserStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,
                                          RedirectAttributes redirectAttributes) {
        Boolean updatedUser = userService.updateUserAccountStatus(id, status);
        if (updatedUser) {
            redirectAttributes.addFlashAttribute("successMessage", "Account Status Updated");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Something wrong on server");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        List<ProductOrder> orders = orderService.getAllProductOrder();
        model.addAttribute("orders", orders);
        model.addAttribute("srch", false);
        return "/admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st,
                                    RedirectAttributes redirectAttributes) {
        OrderStatus[] values = OrderStatus.values();
        System.out.println("values: " + Arrays.toString(values));
        String status = null;
        for (OrderStatus orderStatus : values) {
            if (orderStatus.getId().equals(st)) {
                status = orderStatus.getName();
            }
        }
        ProductOrder updateOrderStatus = orderService.updateOrderStatus(id, status);
        commonUtil.sendMailForProductOrder(updateOrderStatus, status);
        if (!ObjectUtils.isEmpty(updateOrderStatus)) {
            redirectAttributes.addFlashAttribute("successMessage", "Status Updated");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Status not updated");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String productOrderId, Model model,
                                RedirectAttributes redirectAttributes) {
        if (productOrderId == null || productOrderId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order ID cannot be empty.");
            return "redirect:/admin/orders";
        }
        ProductOrder productOrder = orderService.getProductOrderByOrderId(productOrderId);
        System.out.println("productOrder: " + productOrder);
        if (ObjectUtils.isEmpty(productOrder)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect orderId");
            return "redirect:/admin/orders";
        }
        model.addAttribute("productOrder", productOrder);
        model.addAttribute("srch", true);
        return "redirect:/admin/orders";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model model) {
        List<Product> products = productService.searchProduct(ch);
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/add-admin")
    public String addAdmin() {
        return "admin/add_admin";
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute Users user,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           RedirectAttributes redirectAttributes) throws IOException {
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        Users savedUser = userService.saveAdmin(user);
        if (!ObjectUtils.isEmpty(savedUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
                        File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            redirectAttributes.addFlashAttribute("successMsg", "Register successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Something wrong on server");
        }
        return "admin/add_admin";
    }

}