package com.zomato.user_service.service;

import com.zomato.user_service.dto.communation.forRestaurantService.RestaurantsListDto;
import com.zomato.user_service.entity.CustomerAddress;
import com.zomato.user_service.entity.Users;
import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import com.zomato.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UtilityUserService {
    @Autowired
    private UserRepository userRepository;
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
            if(user.getRole()==Role.RIDER)
                return user.getRiderDetails().getCurrentLatitude();
            else if(user.getRole()==Role.RESTAURANT_MANAGER)
                return user.getRestaurantDetails().getLatitude();
            else
            {
                for(CustomerAddress address:user.getCustomerAddressList())
                {
                    if(address.getIsDefault()==true)
                        return address.getLatitude();
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
            if(user.getRole()==Role.RIDER)
                return user.getRiderDetails().getCurrentLongitude();
            else if(user.getRole()==Role.RESTAURANT_MANAGER)
                return user.getRestaurantDetails().getLongitude();
            else
            {
                for(CustomerAddress address:user.getCustomerAddressList())
                {
                    if(address.getIsDefault()==true)
                        return address.getLongitude();
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
}
