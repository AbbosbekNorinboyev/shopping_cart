package uz.pdp.shopping_cart.util;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uz.pdp.shopping_cart.model.ProductOrder;

@Component
public class CommonUtil {
    private final JavaMailSender javaMailSender;

    public CommonUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public Boolean sendSimpleMail(String url, String reciepentEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("norinboyevabboskhan002@gmail.com", "Shooping Cart");
            helper.setTo(reciepentEmail);

            String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
                    + "\">Change my password</a></p>";

            helper.setSubject("Password Reset");
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String stringUrl = request.getRequestURI();
        return stringUrl.replace(request.getServletPath(), "");
    }

    String messageHTML = null;

    @Async
    public Boolean sendMailForProductOrder(ProductOrder productOrder, String status) {
        try {
            messageHTML = "<p>Hello [[name]],</p>" +
                    "<p>Thank you order <b>[[orderStatus]]</b>.</p>" +
                    "<p><b>Product Details</b></p>" +
                    "<p>Name : [[${productName}]]</p>" +
                    "<p>Category : [[${category}]]</p>" +
                    "<p>Quantity : [[${quantity}]]</p>" +
                    "<p>Price : [[${price}]]</p>" +
                    "<p>Payment Type : [[${paymentType}]]</p>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("norinboyevabboskhan002@gmail.com", "Shooping Cart");
            helper.setTo(productOrder.getOrderAddress().getEmail());

            messageHTML = messageHTML.replace("[[name]]", productOrder.getOrderAddress().getFirstName());
            messageHTML = messageHTML.replace("[[orderStatus]]", status);
            messageHTML = messageHTML.replace("[[productName]]", productOrder.getProduct().getTitle());
            messageHTML = messageHTML.replace("[[category]]", productOrder.getProduct().getCategory());
            messageHTML = messageHTML.replace("[[quantity]]", productOrder.getQuantity().toString());
            messageHTML = messageHTML.replace("[[price]]", productOrder.getPrice().toString());
            messageHTML = messageHTML.replace("[[paymentType]]", productOrder.getPaymentType());

            helper.setSubject("Product Ordered Status");
            helper.setText(messageHTML, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
