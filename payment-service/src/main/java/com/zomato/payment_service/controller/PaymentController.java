package com.zomato.payment_service.controller;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.zomato.payment_service.dto.OrderPaymentDto;
import com.zomato.payment_service.entity.Payment;
import com.zomato.payment_service.enums.PaymentStatus;
import com.zomato.payment_service.repository.PaymentRepository;
import com.zomato.payment_service.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

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
    private KafkaTemplate<String,OrderPaymentDto> kafkaTemplate;
    /**
     * Create Razorpay payment link
     */
    @PostMapping("/create-link")
    public void createPaymentLink(@RequestParam("orderId") String orderId,
                                                             @RequestParam("amount") double amount,
                                                             @RequestParam("email") String email,
                                                             @RequestParam("contact") String contact) {
        paymentService.createPaymentLink(orderId, amount, email, contact);
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
                JSONObject notes = paymentEntity.getJSONObject("notes");
                String orderId = notes.getString("custom_order_id");

                String bankTransactionId = "N/A";
                if (paymentEntity.has("acquirer_data")) {
                    JSONObject acquirerData = paymentEntity.getJSONObject("acquirer_data");
                    bankTransactionId = acquirerData.optString("bank_transaction_id", "N/A");
                }

                Payment payment = new Payment();
                payment.setPaymentId(paymentId);
                payment.setOrderId(orderId);
                payment.setStatus("captured".equals(paymentStatus) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
                payment.setAmount(paymentEntity.getDouble("amount") / 100.0);
                payment.setCurrency(paymentEntity.getString("currency"));
                payment.setMethod(paymentEntity.getString("method"));
                payment.setBank(paymentEntity.getString("bank"));
                payment.setBankTransactionId(bankTransactionId);

                repository.save(payment);


                // 2. SEND KAFKA EVENT
                OrderPaymentDto paymentDto = OrderPaymentDto.builder()
                        .orderId(orderId)
                        .paymentId(paymentId)
                        .status("captured".equals(paymentStatus) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                        .build();

                kafkaTemplate.send(topic, orderId, paymentDto);  // KAFKA SEND!
                // ========== EDIT END ==========

                System.out.println("✅ Payment saved & Kafka sent for Order: " + orderId);
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