
package com.zomato.payment_service.service;


import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    // Create payment link
    public void createPaymentLink(String orderId, double amount, String customerEmail, String customerContact) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", (int) Math.round(amount * 100)); // in paise
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("description", "Payment for Order ID: " + orderId);

            JSONObject notes = new JSONObject();
            notes.put("custom_order_id", orderId);
            paymentLinkRequest.put("notes", notes);

            JSONObject customer = new JSONObject();
            customer.put("email", customerEmail);
            customer.put("contact", customerContact);
            paymentLinkRequest.put("customer", customer);

            paymentLinkRequest.put("notify", new JSONObject().put("sms", true).put("email", true));
            paymentLinkRequest.put("callback_url", "https://7137e9201dd7.ngrok-free.app/payments/success");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink link = client.paymentLink.create(paymentLinkRequest);

            String linkId = link.get("id");
            String shortUrl = link.get("short_url");

            log.info("✅ Payment link created & SMS sent for Order: {}", orderId);

        } catch (RazorpayException e) {
            log.error("❌ Payment link failed for Order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Payment link creation failed", e);
        }
    }

}