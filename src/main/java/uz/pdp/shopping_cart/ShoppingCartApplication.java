package uz.pdp.shopping_cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "uz.pdp.shopping_cart") // Barcha kerakli paketlarni ko'rsatish
public class ShoppingCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartApplication.class, args);
	}
	// 2003 user
	// 3456 user2
	// 2210 admin
	// 2802 admin2
}
