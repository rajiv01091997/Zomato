
package com.zomato.payment_servcie.service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.zomato.payment_servcie.dto.PaymentResponse;
import com.zomato.payment_servcie.enums.PaymentStatus;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    // Create payment link
    public PaymentResponse createPaymentLink(String orderId, int amount, String customerEmail, String customerContact) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount * 100); // in paise
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("description", "Payment for Order ID: " + orderId);

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

            return new PaymentResponse(orderId, linkId, shortUrl, PaymentStatus.PENDING);

        } catch (RazorpayException e) {
            e.printStackTrace();
            return new PaymentResponse(orderId, null, null, PaymentStatus.FAILED);
        }
    }

}