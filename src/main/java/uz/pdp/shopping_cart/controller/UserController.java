package uz.pdp.shopping_cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.shopping_cart.model.*;
import uz.pdp.shopping_cart.repository.UserRepository;
import uz.pdp.shopping_cart.service.CartService;
import uz.pdp.shopping_cart.service.CategoryService;
import uz.pdp.shopping_cart.service.OrderService;
import uz.pdp.shopping_cart.service.UserService;
import uz.pdp.shopping_cart.util.CommonUtil;
import uz.pdp.shopping_cart.util.OrderStatus;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final CommonUtil commonUtil;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

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

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer userId,
                            RedirectAttributes redirectAttributes) {
        Cart savedCart = cartService.saveCart(productId, userId);
        if (ObjectUtils.isEmpty(savedCart)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product add to cart failed");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Product add to cart successfully");
        }
        return "redirect:/view_product/" + productId;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {
        Users user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getAllCart(user.getId());
        model.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cartId) {
        cartService.updateCartQuantity(sy, cartId);
        return "redirect:/user/cart";
    }

    private Users getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email);
    }

    @GetMapping("/orders")
    public String orderPage(Principal principal, Model model) {
        Users user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getAllCart(user.getId());
        model.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            model.addAttribute("orderPrice", orderPrice);
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/saveOrders")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal principal) {
        System.out.println("orderRequest: " + orderRequest);
        Users user = getLoggedInUserDetails(principal);
        orderService.saveProductOrder(user.getId(), orderRequest);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }

    @GetMapping("/userOrders")
    public String myOrder(Model model, Principal principal) {
        Users user = getLoggedInUserDetails(principal);
        List<ProductOrder> productOrders = orderService.getAllProductOrderByUser(user.getId());
        model.addAttribute("productOrders", productOrders);
        return "/user/myOrders";
    }

    @GetMapping("/update-status")
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
        if (!ObjectUtils.isEmpty(updateOrderStatus)) {
            commonUtil.sendMailForProductOrder(updateOrderStatus, status);
            redirectAttributes.addFlashAttribute("successMessage", "Status Updated");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Status mot updated");
        }
        return "redirect:/user/userOrders";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Users user, @RequestParam MultipartFile img,
                                RedirectAttributes redirectAttributes) {
        Users updatedUserProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updatedUserProfile)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Profile not updated");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        Users user = getLoggedInUserDetails(principal);
        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodePassword);
            Users updatedUser = userService.updateUser(user);
            if (ObjectUtils.isEmpty(updatedUser)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password not updated");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Password Successfully Updated");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Current Password Incorrect");
        }
        return "redirect:/user/profile";
    }
}
