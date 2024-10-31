package uz.pdp.shopping_cart.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Service
public class CommonServiceImpl implements CommonService {
    @Override
    public void removeSessionMessage() {
        HttpServletRequest request = ((ServletRequestAttributes) (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("successMessage");
        session.removeAttribute("errorMessage");
    }
}