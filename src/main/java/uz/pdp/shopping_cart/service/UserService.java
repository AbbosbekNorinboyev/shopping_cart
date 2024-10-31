package uz.pdp.shopping_cart.service;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.shopping_cart.model.Users;

import java.security.Principal;
import java.util.List;

public interface UserService {
    Users saveUser(@NonNull Users users);
    Users getUserByEmail(@NonNull String email);

    List<Users> getUsers(@NonNull String role);
    Boolean updateUserAccountStatus(Integer id, Boolean status);

    void increaseFailedAttempt(Users user);

    void userAccountLock(Users user);
    Boolean unlockAccountTimeExpired(Users user);
    void resetAttempt(@NonNull Integer userId);
    void updateUserResetToken(@NonNull String email,@NonNull String resetToken);
    Users getUserByToken(@NonNull String token);

    Users updateUser(@NonNull Users user);
    Users updateUserProfile(@NonNull Users user, MultipartFile img);

    Users saveAdmin(@NonNull Users user);
}
