package com.zomato.payment_servcie.controller;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.zomato.payment_servcie.dto.PaymentResponse;
import com.zomato.payment_servcie.enums.PaymentStatus;
import com.zomato.payment_servcie.repository.PaymentRepository;
import com.zomato.payment_servcie.service.PaymentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository repository;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Value("${payment.topic.name}")
    private String topic;

    @Autowired
    private KafkaTemplate<UUID,String> kafkaTemplate;
    /**
     * Create Razorpay payment link
     */
    @PostMapping("/create-link")
    public ResponseEntity<PaymentResponse> createPaymentLink(@RequestParam("orderId") String orderId,
                                                             @RequestParam("amount") int amount,
                                                             @RequestParam("email") String email,
                                                             @RequestParam("contact") String contact) {
        PaymentResponse response = paymentService.createPaymentLink(orderId, amount, email, contact);
        if (response.getStatus() == PaymentStatus.FAILED) {
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Razorpay webhook endpoint
     * Receives asynchronous payment notifications
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String razorpaySignature,
            @RequestBody String payload) {

        System.out.println("==== Incoming Webhook Payload ====");
        System.out.println(payload);
        System.out.println("==== Signature Header ====");
        System.out.println(razorpaySignature);

        if (razorpaySignature == null) {
            // Razorpay signature header missing
            System.err.println("⚠️ Missing X-Razorpay-Signature header!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing X-Razorpay-Signature header");
        }

        try {
            Utils.verifyWebhookSignature(payload, razorpaySignature, razorpaySecret);

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            if ("payment.captured".equals(eventType) || "payment.failed".equals(eventType)) {
                JSONObject paymentEntity = event.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                String paymentId = paymentEntity.getString("id");
                String paymentStatus = paymentEntity.getString("status");
                String description = paymentEntity.optString("description");

                System.out.println("✅ Webhook Received:");
                System.out.println("Payment ID: " + paymentId);
                System.out.println("Status: " + paymentStatus);
                System.out.println("Description: " + description);



            }

            return ResponseEntity.ok("Webhook processed");
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }


    /**
     * Optional: callback page after user completes payment
     */
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccessPage() {
        return ResponseEntity.ok("Payment completed! Thank you.");
    }
}