package uz.pdp.shopping_cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.shopping_cart.model.Users;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByEmail(String email);
    List<Users> findByRole(String role);

    Users findByResetToken(String resetToken);
}