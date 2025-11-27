package com.zomato.order_service.service;


import com.zomato.order_service.dto.GenerateIds;
import com.zomato.order_service.dto.PlaceOrderRequestDto;
import com.zomato.order_service.dto.PlaceOrderResponseDto;
import com.zomato.order_service.dto.feign.fetch.cart.CartBridgeDto;
import com.zomato.order_service.dto.feign.fetch.cart.CartStatus;
import com.zomato.order_service.dto.feign.fetch.cart.ItemBridgeDto;
import com.zomato.order_service.dto.feign.fetch.coupon.CouponBridgeDto;
import com.zomato.order_service.dto.feign.fetch.coupon.DiscountType;
import com.zomato.order_service.dto.feign.fetch.invoice.InvoiceDto;
import com.zomato.order_service.dto.feign.fetch.invoice.ItemDto;
import com.zomato.order_service.dto.feign.fetch.map.RequestDto;
import com.zomato.order_service.dto.feign.fetch.map.ResponseDto;
import com.zomato.order_service.entity.Sequence;
import com.zomato.order_service.feign.*;
import com.zomato.order_service.repository.OrderRepository;
import com.zomato.order_service.repository.SequenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService implements OrderServiceInterface {
    @Value("${platform.fee}")
    private double platformFee;

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
    public PlaceOrderResponseDto place(PlaceOrderRequestDto requestDto)
    {
        //fetch from JWT
        UUID ownerId= UUID.fromString("jbfjjbfjfefjejfefjefee");
        CartBridgeDto cart=cartServiceClient.getCartDetails(requestDto.getCartId());

        //check if cart with given cartId is present or not
         if(cart==null)
             throw new RuntimeException("No Such Cart with given cartId exist");

        UUID restaurantId=cart.getRestaurantId();
        UUID customerId=cart.getCustomerId();

        //compare the ownerId with customerId from cart if this cart is his or not
         if(customerId!=ownerId)
             throw new RuntimeException("You are not the owner of this cart, please check your cartId");
        //check if cart is active or not

         if(cart.getStatus()!= CartStatus.ACTIVE)
             throw new RuntimeException("That's not active, check correct cartId or make another Cart if you don't have a active cart");
        //compare each items price in cart with current price in menu
        //if okay then proceed else ask to re-update cart with latest prices
         if(!cartServiceClient.isSameCurrentAmountFromMenuToCartAmount(requestDto.getCartId()))
             throw new RuntimeException("Pricing for item/items have changed since you added them to your cart \n Either delete this cart and make new one or remove items and add them again");
         //get total amount
          double totalAmount=cart.getTotalAmount();
          double grossTotal=totalAmount;
         //check if any coupon applied on this cart
        String couponCode=cart.getCouponCode();//if present get couponCode

        //if coupon is applied
        double discountAmount=0;
         if(couponCode!=null) {
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
             if ((couponBridgeDto.getCurrentUsageCount() >= couponBridgeDto.getOverallUsageCount()))
                 throw new RuntimeException("usage limit reached for coupon, try other or proceed without coupon");
//               //check if total amount greater or equal to minimumAmount required for coupon
//               //else ask to apply eligible coupon in cart as per amount or update cart without coupon and continue
//             if(!couponServiceClient.isCouponAllowedOnGivenAmount(couponId))
//                 throw new RuntimeException("You have insufficient total to apply this coupon, add more items in cart");
             //apply discount
             double discountValue = couponBridgeDto.getDiscountValue();
             if (couponBridgeDto.getDiscountType() == DiscountType.FLAT) {
                 totalAmount -= discountValue;
                 discountAmount=discountValue;
             } else {
                 double amount = (discountValue * totalAmount) / 100;
                 totalAmount -= amount;
                 discountAmount=amount;
             }
         }
         //get longs and lats of restaurant and customer from User-service
         double latitude1= userServiceClient.getLatitudeOfUser(restaurantId);
         double longitude1=userServiceClient.getLongitudeOfUser(restaurantId);
         double latitude2= userServiceClient.getLatitudeOfUser(customerId);
         double longitude2=userServiceClient.getLongitudeOfUser(customerId);
        RequestDto request=new RequestDto(latitude1,longitude1,latitude2,longitude2);
        ResponseDto responseDto=mapServiceClient.calculateDistance(request);
        double distance=Double.valueOf(responseDto.getDistance());
        int duration=parseDurationToMinutes(responseDto.getDuration());
        double charges=calculateCharges(distance,duration);
         //Add delivery charges+surge charges+18%gst+platformFee
         totalAmount+=charges+platformFee;

         double gst=totalAmount*0.18;
         totalAmount+=gst;
        //send for payment if received FAILED then save into repo with FAILED status
        //also populate other relevant fields before saving


        //if SUCCESS payment then populate relevant fields and save order

        //generate OrderId and InvoiceId
        GenerateIds generateIds=generateInvoiceAndOrderIds();
        String invoiceNumber=generateIds.getInvoiceNumber();
        String orderId=generateIds.getOrderId();
         //save order




        //prepare data for invoice generation for invoice-service
        InvoiceDto invoiceDto=new InvoiceDto();
        invoiceDto.setInvoiceNumber(invoiceNumber);
        invoiceDto.setOrderId(orderId);
        invoiceDto.setCustomerName(userServiceClient.getUserNameOfUser(customerId));
        invoiceDto.setCustomerAddress(userServiceClient.getAddressOfUser(customerId));
        invoiceDto.setRestaurantName(userServiceClient.getRestaurantNameOfUser(restaurantId));
        invoiceDto.setRestaurantAddress(userServiceClient.getAddressOfUser(restaurantId));
        invoiceDto.setGstAmount(gst);
        invoiceDto.setPlatformFee(platformFee);
        invoiceDto.setDeliveryCharge(charges);
        invoiceDto.setCouponDiscount(discountAmount);
        invoiceDto.setTotalPayable(totalAmount);
        invoiceDto.setSubtotal(grossTotal);
        //invoiceDto.setInvoiceDate(); fetch from order createdtime
        invoiceDto.setCustomerEmail(userServiceClient.getEmailOfUser(customerId));
        invoiceDto.setRestaurantEmail(userServiceClient.getEmailOfUser(restaurantId));
        invoiceDto.setCustomerContact(userServiceClient.getPhoneNumberOfUser(customerId));
        invoiceDto.setRestaurantContact(userServiceClient.getPhoneNumberOfUser(restaurantId));
        List<ItemDto> itemDtoList=new ArrayList<>();
        for(ItemBridgeDto itemBridgeDto:cart.getItemBridgeDtoList())
        {
            ItemDto itemDto = new ItemDto();
            itemDto.setQuantity(itemBridgeDto.getQuantity());
            itemDto.setName(menuServiceClient.getItemNameByItemId(itemBridgeDto.getItemId()).get());
            itemDto.setUnitPrice(menuServiceClient.getPriceByItemId(itemBridgeDto.getItemId()).get());
            int quantity=itemBridgeDto.getQuantity();
            double unitPrice=menuServiceClient.getPriceByItemId(itemBridgeDto.getItemId()).get();
            double subtotal=quantity*unitPrice;
            itemDto.setSubtotal(subtotal);
            itemDtoList.add(itemDto);
        }

        //call invoice service to generate invoice and further call mail service to send order acknowledgement
         invoiceServiceClient.generateInvoice(invoiceDto);

        //notify restaurant
        //notify riders
        return null;
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
            charge += (distanceKm - 5.0) * perKmCharge;
        }

        // Time-Based Surge: add ₹1/min for time above 30 mins
        double surge = 0.0;
        if (durationMinutes > 30) {
            surge = (durationMinutes - 30) * surgePerMinute;
            charge += surge;
        }

        // Peak Hour Surge: add 20% during lunch/dinner
        if ((LocalTime.now().isAfter(LocalTime.of(12,0)) && LocalTime.now().isBefore(LocalTime.of(14,0))) ||
                (LocalTime.now().isAfter(LocalTime.of(19,0)) && LocalTime.now().isBefore(LocalTime.of(21,0)))) {
            charge *= 1.2; // 20% surge
        }

        return charge;
    }



}

