package com.prash.social_media_platform.controller;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.PayPalService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private UserService userService;

    @GetMapping("/upgrade")
    public String showPaymentPage() {
        return "payment";
    }

    @PostMapping("/create")
    public String createPayment(Authentication auth) {
        try {
            Payment payment = payPalService.createPayment(
                10.00, "USD", "Pro Tier Upgrade",
                "http://localhost:8080/payment/cancel",
                "http://localhost:8080/payment/success"
            );

            return "redirect:" + payment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst().get().getHref();

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return "redirect:/payment/upgrade?error";
        }
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId,
                                 Authentication auth) {
        try {
            payPalService.executePayment(paymentId, payerId);
            userService.upgradeToPro(auth.getName());
            return "redirect:/user/profile?pro=success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/payment/upgrade?error";
        }
    }

    @GetMapping("/cancel")
    public String paymentCancelled() {
        return "redirect:/user/profile?cancelled=true";
    }
}
