//package uz.pdp.shopping_cart.config;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//import uz.pdp.shopping_cart.model.Users;
//import uz.pdp.shopping_cart.repository.UserRepository;
//import uz.pdp.shopping_cart.service.UserService;
//import uz.pdp.shopping_cart.util.AppConstant;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
//    private final UserRepository userRepository;
//    private final UserService userService;
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        AuthenticationException exception) throws IOException, ServletException {
//        String email = request.getParameter("username");
//        Users user = userRepository.findByEmail(email);
//        System.out.println("user: " + user);
//        if (user.getIsEnable()) {
//            if (user.getAccountNonLocked()) {
//                if (user.getFailedAttempt() <= AppConstant.ATTEMPT_TIMES) {
//                    userService.increaseFailedAttempt(user);
//                } else {
//                    userService.userAccountLock(user);
//                    exception = new LockedException("Your account is locked!! Failed attempt 3");
//                }
//            } else {
//                if (userService.unlockAccountTimeExpired(user)) {
//                    exception = new LockedException("Your account is locked!! Please try to login");
//                } else {
//                    exception = new LockedException("Your account is locked!! Please try after sometimes");
//                }
//            }
//        } else {
//            exception = new LockedException("Your account is inactive");
//        }
//        super.onAuthenticationFailure(request, response, exception);
//    }
//
//}
