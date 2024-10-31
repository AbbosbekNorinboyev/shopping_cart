package uz.pdp.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.shopping_cart.model.Users;
import uz.pdp.shopping_cart.repository.UserRepository;
import uz.pdp.shopping_cart.util.AppConstant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users saveUser(@NonNull Users user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public Users getUserByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<Users> getUsers(@NonNull String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateUserAccountStatus(Integer id, Boolean status) {
        Optional<Users> userFound = userRepository.findById(id);
        if (userFound.isPresent()) {
            Users user = userFound.get();
            user.setIsEnable(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(Users user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(Users user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockAccountTimeExpired(Users user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTimeMillis = System.currentTimeMillis();
        if (unlockTime < currentTimeMillis) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(@NonNull Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFailedAttempt(0);
            userRepository.save(user);
        });
    }

    @Override
    public void updateUserResetToken(@NonNull String email, @NonNull String resetToken) {
        Users findByEmail = userRepository.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepository.save(findByEmail);
    }

    @Override
    public Users getUserByToken(@NonNull String token) {
        return userRepository.findByResetToken(token);
    }

    @Override
    public Users updateUser(@NonNull Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users updateUserProfile(@NonNull Users user, MultipartFile img) {
        Users dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found: " + user.getId()));
        if (!img.isEmpty()) {
            dbUser.setProfileImage(img.getOriginalFilename());
        }
        if (!ObjectUtils.isEmpty(dbUser)) {
            dbUser.setName(user.getName());
            dbUser.setMobileNumber(user.getMobileNumber());
            dbUser.setEmail(user.getEmail());
            dbUser.setCity(user.getCity());
            dbUser.setState(user.getState());
            dbUser.setPincode(user.getPincode());
            userRepository.save(dbUser);
        }
        if (!img.isEmpty()) {
            File saveFile = null;
            try {
                saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
                        File.separator + img.getOriginalFilename());
                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dbUser;
    }

    @Override
    public Users saveAdmin(@NonNull Users user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }
}
