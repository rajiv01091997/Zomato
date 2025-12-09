package com.zomato.order_service.service;


import com.zomato.order_service.dto.GenerateIds;
import com.zomato.order_service.dto.PlaceOrderRequestDto;
import com.zomato.order_service.dto.PlaceOrderResponseDto;
import com.zomato.order_service.dto.UpdateStatus;
import com.zomato.order_service.dto.feign.fetch.cart.CartBridgeDto;
import com.zomato.order_service.dto.feign.fetch.cart.CartStatus;
import com.zomato.order_service.dto.feign.fetch.cart.ItemBridgeDto;
import com.zomato.order_service.dto.feign.fetch.coupon.CouponBridgeDto;
import com.zomato.order_service.dto.feign.fetch.coupon.DiscountType;
import com.zomato.order_service.dto.feign.fetch.invoice.InvoiceDto;
import com.zomato.order_service.dto.feign.fetch.invoice.ItemDto;
import com.zomato.order_service.dto.feign.fetch.map.RequestDto;
import com.zomato.order_service.dto.feign.fetch.map.ResponseDto;
import com.zomato.order_service.dto.kafka.mail.MailDto;
import com.zomato.order_service.dto.kafka.payment.OrderPaymentDto;
import com.zomato.order_service.dto.kafka.payment.PaymentStatus;
import com.zomato.order_service.entity.Order;
import com.zomato.order_service.entity.Sequence;
import com.zomato.order_service.enums.OrderStatus;
import com.zomato.order_service.feign.*;
import com.zomato.order_service.repository.OrderRepository;
import com.zomato.order_service.repository.SequenceRepository;
import com.zomato.order_service.security.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class OrderService implements OrderServiceInterface {
    @Value("${platform.fee}")
    private double platformFee;

    @Value("${delivery.confirmation.topic}")
    private String deliveryTopic;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private CartServiceClient cartServiceClient;
    @Autowired
    private CouponServiceClient couponServiceClient;
    @Autowired
    private MapServiceClient mapServiceClient;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private MenuServiceClient menuServiceClient;
    @Autowired
    private InvoiceServiceClient invoiceServiceClient;
    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private KafkaTemplate<String, MailDto> kafkaTemplate;

    @PreAuthorize("hasRole('CUSTOMER')")
    public PlaceOrderResponseDto place(PlaceOrderRequestDto requestDto) {
        //fetched from JWT
        UUID ownerId = ((CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CartBridgeDto cart = cartServiceClient.getCartDetails(requestDto.getCartId());

        //check if cart with given cartId is present or not
        if (cart == null)
            throw new RuntimeException("No Such Cart with given cartId exist");

        UUID restaurantId = cart.getRestaurantId();
        UUID customerId = cart.getCustomerId();
        //compare the ownerId with customerId from cart if this cart is his or not
        if (!customerId.equals(ownerId))
            throw new RuntimeException("You are not the owner of this cart, please check your cartId");
        //check if cart is active or not
        if (cart.getStatus() != CartStatus.ACTIVE)
            throw new RuntimeException("Cart already checked out, check correct cartId or make another Cart if you don't have a active cart");
        //compare each items price in cart with current price in menu
        //if okay then proceed else ask to re-update cart with latest prices
        if (!cartServiceClient.isSameCurrentAmountFromMenuToCartAmount(requestDto.getCartId()))
            throw new RuntimeException("Pricing for item/items have changed since you added them to your cart \n Either delete this cart and make new one or remove items and add them again");
        //get total amount
        double totalAmount = cart.getTotalAmount();
        double grossAmount = cart.getGrossAmount();
        log.info("total amount discount ke baad:{}", totalAmount);
        log.info("gross amount bin discount ke:{}", grossAmount);
        //check if any coupon applied on this cart
        String couponCode = cart.getCouponCode();//if present get couponCode

        //if coupon is applied
        double discountAmount = 0;
        if (couponCode != null) {
            //check if coupon code is still present in coupon-service for given restaurantId
            CouponBridgeDto couponBridgeDto = couponServiceClient.getCouponWithCouponCodeAndRestaurant(couponCode, restaurantId);
            if (couponBridgeDto == null)
                throw new RuntimeException("Applied coupon doesn't exist anymore, maybe it is removed, try other relevant coupons or make cart without coupon and proceed");
            //get couponId for given couponcode and restaurantId
            UUID couponId = couponBridgeDto.getId();
            //check coupon is active or not
            if (!couponBridgeDto.getIsActive())
                throw new RuntimeException("Coupon is not active anymore, try other coupon or proceed by creating a cart without coupon");


            //check if coupon maxusage limit is greater than current usage
            //else ask to remove coupon in cart or add other coupons and continue
//             if ((couponBridgeDto.getCurrentUsageCount() >= couponBridgeDto.getOverallUsageCount()))
//                 throw new RuntimeException("usage limit reached for coupon, try other or proceed without coupon");
//               //check if total amount greater or equal to minimumAmount required for coupon
//               //else ask to apply eligible coupon in cart as per amount or update cart without coupon and continue
//             if(!couponServiceClient.isCouponAllowedOnGivenAmount(couponId))
//                 throw new RuntimeException("You have insufficient total to apply this coupon, add more items in cart");
            //apply discount
            double discountValue = couponBridgeDto.getDiscountValue();
            if (couponBridgeDto.getDiscountType() == DiscountType.FLAT) {
                discountAmount = discountValue;
            } else {
                double amount = Math.round(((discountValue * grossAmount) / 100)*1000.0)/1000.0;
                discountAmount = amount;
            }
            log.info("discount ka value hai:{}", discountAmount);
            if (grossAmount - discountAmount != totalAmount) {
                log.info("grossAmount-discountValue:{}", grossAmount - discountValue);
                log.info("total:{}", totalAmount);
                throw new RuntimeException("Coupon has been edited, Add latest version of coupon and recreate cart");
            }
        }
        //get longs and lats of restaurant and customer from User-service
        double lat1 = userServiceClient.getLatitudeOfUser(customerId);
        double lat2 = userServiceClient.getLatitudeOfUser(restaurantId);
        double long1 = userServiceClient.getLongitudeOfUser(customerId);
        double long2 = userServiceClient.getLongitudeOfUser(restaurantId);

        log.info("customer{}{},restaurant{}{}", lat1, long1, lat2, long2);

        RequestDto request = new RequestDto(lat1, long1, lat2, long2);
        ResponseDto responseDto = mapServiceClient.calculateDistance(request);
        String distanceStr = responseDto.getDistance().replace(" km", "");
        double distance = Double.parseDouble(distanceStr);
        int duration = parseDurationToMinutes(responseDto.getDuration());
        double charges = calculateCharges(distance, duration);
        //Add delivery charges+surge charges+18%gst+platformFee
        totalAmount += charges + platformFee;

        double gst = totalAmount * 0.18;
        totalAmount += gst;

        //generate OrderId and InvoiceId
        GenerateIds generateIds = generateInvoiceAndOrderIds();
        String invoiceNumber = generateIds.getInvoiceNumber();
        String orderId = generateIds.getOrderId();
        //Save a order with pending status
        Order order = Order.builder()
                .id(orderId)
                .cartId(cart.getCartId())
                .restaurantId(cart.getRestaurantId())
                .couponCode(cart.getCouponCode() != null ? cart.getCouponCode() : null)
                .customerId(cart.getCustomerId())
                .orderStatus(OrderStatus.PENDING)
                .deliveryAddress(userServiceClient.getAddressOfUser(cart.getCustomerId()))
                .specialInstructions(requestDto.getSpecialInstructions())
                .paymentStatus(PaymentStatus.PENDING)
                .invoiceNumber(invoiceNumber)
                .totalAmount(totalAmount)
                .grossAmount(grossAmount)
                .deliveryCharge(charges)
                .couponDiscount(discountAmount)
                .platformFee(platformFee)
                .gstAmount(gst)
                .build();
        //save order
        Order savedOrder = orderRepository.save(order);
        //send for payment
        //?????later also make to send email of payment link as of now only going on phone
        paymentServiceClient.createPaymentLink(savedOrder.getId(), savedOrder.getTotalAmount(),
                userServiceClient.getEmailOfUser(customerId), userServiceClient.getPhoneNumberOfUser(customerId));

        //notify restaurant
        //notify riders
        // End me ye add karo:
        //?????make this proper later
        return PlaceOrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(savedOrder.getTotalAmount())
                .build();

    }

    @KafkaListener(topics = "${payment.webhook.topic}", groupId = "order-service-group")
    @Transactional
    public void updatePaymentDetails(ConsumerRecord<String, OrderPaymentDto> record) {
        OrderPaymentDto paymentDto = record.value();

        Optional<Order> orderOpt = orderRepository.findById(paymentDto.getOrderId());
        if (orderOpt.isEmpty()) {
            log.error("Order not found: {}", paymentDto.getOrderId());
            return;
        }

        Order order = orderOpt.get();

        if (paymentDto.getStatus().equals("FAILED")) {//since failed so update status and update invoice null for failed payment
            order.setOrderStatus(OrderStatus.FAILED);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setInvoiceNumber(null);
            orderRepository.save(order);
            log.error("Payment failed!!!!,please try again");
            return;
        }
        //first of all transition this cart to checked_out
        cartServiceClient.changeStatus(order.getCartId());
        //update status and save
        order.setPaymentId(paymentDto.getPaymentId());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        String otp = generateOtp();
        order.setOtp(otp);
        //find feasibleRider and save riderId
        log.info("calling user-service for getting feasible rider id");
        UUID riderId = userServiceClient.getFeasibleRider(order.getRestaurantId());
        order.setRiderId(riderId);
         log.info("saving order");
        Order saved = orderRepository.save(order);
        log.info("order saved");

        //now trigger invoice service to prepare invoice and send mail
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNumber(saved.getInvoiceNumber());
        invoiceDto.setOrderId(saved.getId());
        invoiceDto.setCustomerName(userServiceClient.getUserNameOfUser(saved.getCustomerId()));
        invoiceDto.setCustomerAddress(userServiceClient.getAddressOfUser(saved.getCustomerId()));
        invoiceDto.setRestaurantName(userServiceClient.getRestaurantNameOfUser(saved.getRestaurantId()));
        invoiceDto.setRestaurantAddress(userServiceClient.getAddressOfUser(saved.getRestaurantId()));
        invoiceDto.setGstAmount(saved.getGstAmount());
        invoiceDto.setPlatformFee(saved.getPlatformFee());
        invoiceDto.setDeliveryCharge(saved.getDeliveryCharge());
        invoiceDto.setCouponDiscount(saved.getCouponDiscount());
        invoiceDto.setTotalPayable(saved.getTotalAmount());
        invoiceDto.setSubtotal(saved.getGrossAmount());
        invoiceDto.setInvoiceDate(saved.getOrderTime());
        invoiceDto.setCustomerEmail(userServiceClient.getEmailOfUser(saved.getCustomerId()));
        invoiceDto.setRestaurantEmail(userServiceClient.getEmailOfUser(saved.getRestaurantId()));
        invoiceDto.setCustomerContact(userServiceClient.getPhoneNumberOfUser(saved.getCustomerId()));
        invoiceDto.setRestaurantContact(userServiceClient.getPhoneNumberOfUser(saved.getRestaurantId()));
        invoiceDto.setRiderEmail(userServiceClient.getEmailOfUser(saved.getRiderId()));
        invoiceDto.setRiderName(userServiceClient.getUserNameOfUser(saved.getRiderId()));
        invoiceDto.setRiderOtp(otp);
        //get cart
        CartBridgeDto cart = cartServiceClient.getCartDetails(saved.getCartId());
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (ItemBridgeDto itemBridgeDto : cart.getItemBridgeDtoList()) {
            ItemDto itemDto = new ItemDto();
            itemDto.setQuantity(itemBridgeDto.getQuantity());
            itemDto.setName(menuServiceClient.getItemNameByItemId(itemBridgeDto.getItemId()).get());
            itemDto.setUnitPrice(menuServiceClient.getPriceByItemId(itemBridgeDto.getItemId()).get());
            int quantity = itemBridgeDto.getQuantity();
            double unitPrice = menuServiceClient.getPriceByItemId(itemBridgeDto.getItemId()).get();
            double subtotal = quantity * unitPrice;
            itemDto.setSubtotal(subtotal);
            itemDtoList.add(itemDto);
        }
        invoiceDto.setItems(itemDtoList);
        //call invoice service to generate invoice and further call mail service to send order acknowledgement
        invoiceServiceClient.generateInvoice(invoiceDto);
        log.info("✅ Invoice generated & emailed for Order: {}", saved.getId());  // ← YE OPTIONAL


        cartServiceClient.changeStatus(order.getCartId());
        log.info("changed cart status to CHECKED_OUT");
    }

    @Transactional
    public synchronized GenerateIds generateInvoiceAndOrderIds() {

        Long SEQUENCE_ID = 1L;

        // 1. Fetch current sequence from DB or create initial one
        Optional<Sequence> seqOpt = sequenceRepository.findById(SEQUENCE_ID);
        Sequence sequence;
        if (seqOpt.isPresent()) {
            sequence = seqOpt.get();
            sequence.setCounter(sequence.getCounter() + 1);
        } else {
            sequence = new Sequence();
            sequence.setId(SEQUENCE_ID);
            sequence.setCounter(1L);
        }

        // 2. Save updated sequence back to DB
        sequenceRepository.save(sequence);

        // 3. Format invoice and order IDs
        Long seqValue = sequence.getCounter();
        String datePrefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD format

        String invoiceId = String.format("INV-%s-%06d", datePrefix, seqValue);
        String orderId = String.format("ORD-%s-%06d", datePrefix, seqValue);

        // 4. Return both IDs wrapped in DTO
        return new GenerateIds(invoiceId, orderId);
    }


    //for converting duration into minutes
    public static int parseDurationToMinutes(String durationStr) {
        int minutes = 0;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+) day[s]?").matcher(durationStr);
        if (m.find()) minutes += Integer.parseInt(m.group(1)) * 24 * 60;
        m = java.util.regex.Pattern.compile("(\\d+) hour[s]?").matcher(durationStr);
        if (m.find()) minutes += Integer.parseInt(m.group(1)) * 60;
        m = java.util.regex.Pattern.compile("(\\d+) min[s]?").matcher(durationStr);
        if (m.find()) minutes += Integer.parseInt(m.group(1));
        return minutes;
    }

    //for calculating charges
    public double calculateCharges(double distanceKm, int durationMinutes) {
        double baseCharge = 30.0;
        double perKmCharge = 10.0;
        double surgePerMinute = 1.0;
        double charge = baseCharge;

        // Distance Surcharge: add ₹10/km for distance above 5 km
        if (distanceKm > 5.0) {
            log.info("distance surcharge: {}", (distanceKm - 5.0) * perKmCharge);
            charge += (distanceKm - 5.0) * perKmCharge;
        }

        // Time-Based Surge: add ₹1/min for time above 30 mins
        double surge = 0.0;
        if (durationMinutes > 30) {
            surge = (durationMinutes - 30) * surgePerMinute;
            log.info("Time surcharge added: {}", surge);
            charge += surge;
        }

        // Peak Hour Surge: add 20% during lunch/dinner
        if ((LocalTime.now().isAfter(LocalTime.of(12, 0)) && LocalTime.now().isBefore(LocalTime.of(14, 0))) ||
                (LocalTime.now().isAfter(LocalTime.of(19, 0)) && LocalTime.now().isBefore(LocalTime.of(21, 0)))) {
            log.info("peak hour surge added: {}", charge * 1.2);
            charge *= 1.2; // 20% surge
        }

        return charge;
    }

    public boolean isAllowed(OrderStatus toStatus, String orderId) {
        HashMap<OrderStatus, Set<OrderStatus>> map = new HashMap<>();
        map.put(OrderStatus.PLACED, Set.of(OrderStatus.CONFIRMED,
                OrderStatus.CANCELLED));
        map.put(OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING,
                OrderStatus.READY_FOR_PICKUP,
                OrderStatus.CANCELLED));
        map.put(OrderStatus.PREPARING, Set.of(OrderStatus.READY_FOR_PICKUP,
                OrderStatus.CANCELLED
        ));
        map.put(OrderStatus.READY_FOR_PICKUP, Set.of(OrderStatus.PICKED_UP, OrderStatus.CANCELLED));
        map.put(OrderStatus.PICKED_UP, Set.of(
                OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        map.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        OrderStatus currentStatus = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order with this orderId not present")).getOrderStatus();
        boolean isValid = map.getOrDefault(currentStatus, Set.of()).contains(toStatus);
        if (!isValid) {
            log.error("Not allowed");
            return false;
        }
        String role = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().iterator().next().getAuthority();

        if (toStatus == OrderStatus.CONFIRMED ||
                toStatus == OrderStatus.PREPARING ||
                toStatus == OrderStatus.READY_FOR_PICKUP) {
            return role.equals("ROLE_RESTAURANT_MANAGER");
        }

        if (toStatus == OrderStatus.OUT_FOR_DELIVERY ||
                toStatus == OrderStatus.DELIVERED||toStatus == OrderStatus.PICKED_UP) {
            return role.equals("ROLE_RIDER");
        }

        return true;
    }

    //for generating the otp for rider
    public String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    //update status for restaurant
    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    public String updateRestaurantOrderStatus(UpdateStatus status) {
        UUID restaurantId = ((CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            Order order = orderRepository.findById(status.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order couldn't be found"));

            if(!order.getRestaurantId().equals(restaurantId))
                throw new RuntimeException("This order is not associated with your restaurant");

            if (!isAllowed(OrderStatus.valueOf(status.getToStatus().toUpperCase()), status.getOrderId()))
                return "❌ Invalid transition for restaurant!";

            order.setOrderStatus(OrderStatus.valueOf(status.getToStatus().toUpperCase()));
            orderRepository.save(order);

            return
                    "✅ Status for orderId: " + status.getOrderId() + " updated to " + status.getToStatus();

    }
  //update status for rider
  @PreAuthorize("hasRole('RIDER')")
    public String updateRiderOrderStatus(UpdateStatus status) {
      UUID riderId = ((CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

            Order order = orderRepository.findById(status.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if(!order.getRiderId().equals(riderId))
                throw new RuntimeException("This order is not assigned to you");

            if (!isAllowed(OrderStatus.valueOf(status.getToStatus().toUpperCase()), status.getOrderId()))
                return "❌ Invalid transition for rider!";

            if(status.getToStatus().equals("PICKED_UP")) {
                if (status.getOtp() == null)
                     throw new RuntimeException("OTP is required for verification");

                if(!status.getOtp().trim().isEmpty() &&
                        !status.getOtp().equals(order.getOtp())){
                    throw new RuntimeException("❌ Wrong OTP! Please check and try again.");
                }
            }

            if(status.getToStatus().equals("DELIVERED"))
            {
                log.info("updating delivery time in order since its delivered");
                LocalDateTime deliveryTime=LocalDateTime.now();
                order.setDeliveryTime(deliveryTime);
                log.info("request to change availability status of rider since he is free now");
                userServiceClient.updateAvailabilityStatusOfRider(order.getRiderId());

                log.info("send delivery mail to customer");
                MailDto mail= MailDto.builder()
                        .orderId(order.getId())
                        .email(userServiceClient.getEmailOfUser(order.getCustomerId()))
                        .restaurantName(userServiceClient.getRestaurantNameOfUser(order.getRestaurantId()))
                        .userName(userServiceClient.getUserNameOfUser(order.getCustomerId()))
                        .creationTime(deliveryTime)
                        .build();
                kafkaTemplate.send(deliveryTopic,userServiceClient.getEmailOfUser(order.getCustomerId()),mail);

                log.info("change isAvailable status of rider to true");
                userServiceClient.updateAvailabilityStatusOfRider(riderId);
            }
            order.setOrderStatus(OrderStatus.valueOf(status.getToStatus().toUpperCase()));
            orderRepository.save(order);


            return
                    "✅ Status for orderId: " + status.getOrderId() + " updated to " + status.getToStatus();

    }


}

