package uz.pdp.shopping_cart.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.shopping_cart.model.Category;
import uz.pdp.shopping_cart.model.Product;
import uz.pdp.shopping_cart.model.Users;
import uz.pdp.shopping_cart.service.CartService;
import uz.pdp.shopping_cart.service.CategoryService;
import uz.pdp.shopping_cart.service.ProductServiceImpl;
import uz.pdp.shopping_cart.service.UserService;
import uz.pdp.shopping_cart.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static uz.pdp.shopping_cart.util.CommonUtil.generateUrl;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final ProductServiceImpl productService;
    private final UserService userService;
    private final CommonUtil commonUtil;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

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
        return "indexes";
    }

    @GetMapping("/signIn")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String product(Model model, @RequestParam(value = "category", defaultValue = "") String category,
                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("paramValue", category);
        model.addAttribute("categories", categories);

//        List<Product> products = productService.getAllActiveProduct(category);
//        model.addAttribute("products", products);

        Page<Product> productPagination = productService.getAllActiveProductPagination(page, size, category);
        model.addAttribute("products", productPagination.getContent());
        model.addAttribute("productSize", productPagination.getSize());
        model.addAttribute("pageNumber", productPagination.getNumber());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalElements", productPagination.getTotalElements());
        model.addAttribute("totalPage", productPagination.getTotalPages());
        model.addAttribute("isFirst", productPagination.isFirst());
        model.addAttribute("isLast", productPagination.isLast());
        return "products";
    }

    @GetMapping("/view_product/{id}")
    public String view_product(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute Users users,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           RedirectAttributes redirectAttributes) throws IOException {
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        users.setProfileImage(imageName);
        Users savedUser = userService.saveUser(users);
        if (!ObjectUtils.isEmpty(savedUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
                        File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Register successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong on server");
        }
        return "redirect:/register";
    }

    // Forgot Password Code
    @GetMapping("/forgot_password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        Users user = userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);
            String url = generateUrl(request) + "/reset_password?token=" + resetToken;
            Boolean sendMail = commonUtil.sendSimpleMail(url, email);
            if (sendMail) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Please check your email..Password reset link sent");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Something wrong on server! Email not send");
            }
        }
        return "redirect:/forgot_password";
    }

//    @GetMapping("/reset_password")
//    public String showResetPassword(@RequestParam String token, RedirectAttributes model) {
//        Users userByToken = userService.getUserByToken(token);
//        if (userByToken == null) {
//            model.addAttribute("msg", "Your link is invalid or expired !!");
//            return "message";
//        }
//        model.addAttribute("token", token);
//        return "reset_password";
//    }

    //    @PostMapping("/reset_password")
//    public String resetPassword(@RequestParam String token, @RequestParam String password, RedirectAttributes model) {
//        Users userByToken = userService.getUserByToken(token);
//        if (userByToken == null) {
//            model.addAttribute("errorMsg", "Your link is invalid or expired !!");
//            return "message";
//        } else {
//            userByToken.setPassword(passwordEncoder.encode(password));
//            userByToken.setResetToken(null);
//            userService.updateUser(userByToken);
//            model.addAttribute("msg", "Password change successfully");
//            return "message";
//        }
//    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model model) {
        List<Product> products = productService.searchProduct(ch);
        model.addAttribute("products", products);
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);
        return "products";
    }
}
