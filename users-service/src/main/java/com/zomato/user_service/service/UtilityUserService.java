package com.zomato.user_service.service;

import com.zomato.user_service.dto.communation.forRestaurantService.RestaurantsListDto;
import com.zomato.user_service.dto.feign.map.RequestDto;
import com.zomato.user_service.dto.feign.map.ResponseDto;
import com.zomato.user_service.entity.CustomerAddress;
import com.zomato.user_service.entity.Users;
import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import com.zomato.user_service.feign.MapServiceClient;
import com.zomato.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UtilityUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MapServiceClient mapServiceClient;
    //utility methods for restaurant-service
    public List<RestaurantsListDto> getRestaurantsList()
    {   List<RestaurantsListDto> dtoList=new ArrayList<>();
        List<Users> list=userRepository.findUsersByRoleAndStatus(Role.RESTAURANT_MANAGER, Status.ACTIVE);
        for(Users restaurant:list)
        {
            dtoList.add(
                    RestaurantsListDto.builder()
                            .restaurantId(restaurant.getId())
                            .phoneNumber(restaurant.getPhoneNumber())
                            .email(restaurant.getEmail())
                            .restaurantName(restaurant.getRestaurantDetails().getRestaurantName())
                            .restaurantAddress(restaurant.getRestaurantDetails().getRestaurantAddress())
                            .latitude(restaurant.getRestaurantDetails().getLatitude())
                            .longitude(restaurant.getRestaurantDetails().getLongitude())
                            .businessLicenseNumber(restaurant.getRestaurantDetails().getBusinessLicenseNumber())
                            .workingHours(restaurant.getRestaurantDetails().getWorkingHours())
                            .build()
            );
        }
        return dtoList;
    }

    public Double getLatitude(UUID userId)
    {
        Users user=userRepository.findById(userId).get();
        if(user!=null)
        {
            if(user.getRole()==Role.RIDER) {
                log.info("Rider Latitude:{}",user.getRiderDetails().getCurrentLatitude());
                return user.getRiderDetails().getCurrentLatitude();
            }
            else if(user.getRole()==Role.RESTAURANT_MANAGER) {
                log.info("Restaurant latitude:{}", user.getRestaurantDetails().getLatitude());
                return user.getRestaurantDetails().getLatitude();
            }
            else
            {
                for(CustomerAddress address:user.getCustomerAddressList())
                {
                    if(address.getIsDefault()==true) {
                        log.info("Customer latitude:{}", address.getLatitude());
                        return address.getLatitude();
                    }
                }
            }
        }
        else
            return null;

        return 0.0;//just for compiler
    }
    public Double getLongitude(UUID userId)
    {
        Users user=userRepository.findById(userId).get();
        if(user!=null)
        {
            if(user.getRole()==Role.RIDER) {
                log.info("Rider Longitude:{}", user.getRiderDetails().getCurrentLongitude());
                return user.getRiderDetails().getCurrentLongitude();
            }
            else if(user.getRole()==Role.RESTAURANT_MANAGER) {
                log.info("Restaurant longitude:{}", user.getRestaurantDetails().getLongitude());
                return user.getRestaurantDetails().getLongitude();
            }
            else
            {
                for(CustomerAddress address:user.getCustomerAddressList())
                {
                    if(address.getIsDefault()==true) {
                        log.info("Customer longitude:{}",address.getLongitude());
                        return address.getLongitude();
                    }
                }
            }
        }
        else
            return null;

        return 0.0;//just for compiler
    }
    public String getAddressByUserId(UUID userId) {
        Users user = userRepository.findById(userId).get();
        if (user == null)
            return null;

        if (user.getRole() == Role.RIDER)
            return user.getRiderDetails().getPermanentAddress();
        else if (user.getRole() == Role.RESTAURANT_MANAGER)
            return user.getRestaurantDetails().getRestaurantAddress();
        else {
            for (CustomerAddress address : user.getCustomerAddressList()) {
                if (address.getIsDefault() == true)
                    return address.getAddressLine();
            }
        }

        return "";//for compiler
    }
    public String getUserName(UUID userId)
    {
        Users user = userRepository.findById(userId).get();
        if (user == null)
            return null;

        return user.getUserName();
    }
    public String getRestaurantName(UUID userId)
    {
        Users user = userRepository.findById(userId).get();
        if(user.getRole()!=Role.RESTAURANT_MANAGER)
            return null;
        return user.getRestaurantDetails().getRestaurantName();
    }
    public String getEmail(UUID userId)
    {
        Users user = userRepository.findById(userId).get();
        if(user==null)
            return null;
        return user.getEmail();
    }
    public String getPhoneNumber(UUID userId)
    {
        Users user = userRepository.findById(userId).get();
        if(user==null)
            return null;
        return user.getPhoneNumber();
    }
    public void updateAvailabilityStatusOfRider(UUID userId)
    {
        Users user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("Not a valid userId"));

        if(user.getRole()!=Role.RIDER)
            throw new RuntimeException("User is not a Rider, please check Id");

        if(user.getRiderDetails().getIsAvailable()) {
            user.getRiderDetails().setIsAvailable(false);
            log.info("Availability status of rider changed to false");
        }
        else {
            user.getRiderDetails().setIsAvailable(true);
            log.info("Availability status of rider changed to true");
        }

        userRepository.save(user);
        log.info("Availability status of rider changed");
    }
    @Transactional
    public UUID getFeasibleRiderForDelivery(UUID restaurantId)
    {
       List<Users> list= userRepository.findUsersByRoleAndStatus(Role.RIDER,Status.ACTIVE);
        if (list.isEmpty()) {
            throw new RuntimeException("No active riders available!");
        }
        log.info("size of list "+list.size());
        Users restaurantUser = userRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found!"));

        double restaurantLong = restaurantUser.getRestaurantDetails().getLongitude();
        double restaurantLat = restaurantUser.getRestaurantDetails().getLatitude();

       UUID riderId=null;
       int minTime=Integer.MAX_VALUE;
       for(Users user:list)
       {
           if(user.getRiderDetails().getActiveStatus()==true
            && user.getRiderDetails().getIsAvailable()==true)
           {
              double riderLong= user.getRiderDetails().getCurrentLongitude();
              double riderLat=user.getRiderDetails().getCurrentLatitude();
               RequestDto request=new RequestDto(riderLat,riderLong,restaurantLat,restaurantLong);
             ResponseDto response= mapServiceClient.calculateDistance(request);
               int duration=parseDurationToMinutes(response.getDuration());
               log.info("duration for this rider:{}", duration);
               if(minTime>duration)
               {
                   minTime=duration;
                   riderId=user.getId();
               }
           }
       }
       if(riderId==null)
           throw new RuntimeException("No riders available at the moment, try after some time");
       Users user=userRepository.findById(riderId).orElseThrow(()->new RuntimeException("Rider profile is not present anymore"));
       user.getRiderDetails().setIsAvailable(false);
       log.info("Rider's available status changing to false");
       userRepository.save(user);
       return riderId;
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
}
