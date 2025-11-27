package com.zomato.user_service.service;



import com.zomato.user_service.dto.fetchLoggedInUserProfile.CustomerProfileResponseDto;
import com.zomato.user_service.dto.fetchLoggedInUserProfile.RestaurantProfileResponseDto;
import com.zomato.user_service.dto.fetchLoggedInUserProfile.RiderProfileResponseDto;
import com.zomato.user_service.dto.fetchLoggedInUserProfile.UserProfileResponseDto;
import com.zomato.user_service.dto.login.LoginRequestDto;
import com.zomato.user_service.dto.login.LoginResponseDto;
import com.zomato.user_service.dto.mail.MailDto;
import com.zomato.user_service.dto.signupCustomer.CustomerAddressResponseDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupRequestDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupResponseDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantDetailsResponseDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupRequestDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupResponseDto;
import com.zomato.user_service.dto.signupRider.RiderDetailsResponseDto;
import com.zomato.user_service.dto.signupRider.RiderSignupRequestDto;
import com.zomato.user_service.dto.signupRider.RiderSignupResponseDto;
import com.zomato.user_service.dto.updateLoggedInUser.*;
import com.zomato.user_service.entity.CustomerAddress;
import com.zomato.user_service.entity.RestaurantDetails;
import com.zomato.user_service.entity.RiderDetails;
import com.zomato.user_service.entity.Users;
import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import com.zomato.user_service.repository.UserRepository;
import com.zomato.user_service.security.JWTAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private KafkaTemplate<String, MailDto> kafkaTemplate;

    @Value("${customer.topic.name}")
    private String customerKafkaTopic;
    @Value("${rider.topic.name}")
    private String riderKafkaTopic;
    @Value("${restaurant.topic.name}")
    private String restaurantKafkaTopic;
    @Value("${password.change.topic.name}")
    private String passwordChangeTopic;

    @Override
    public CustomerSignupResponseDto signupCustomer(CustomerSignupRequestDto customerSignupRequestDto) {
        Users user = new Users();
        user.setUserName(customerSignupRequestDto.getUserName());
        user.setPassword(passwordEncoder.encode(customerSignupRequestDto.getPassword()));
        user.setEmail(customerSignupRequestDto.getEmail());
        user.setPhoneNumber(customerSignupRequestDto.getPhoneNumber());
        user.setRole(Role.CUSTOMER);
        user.setStatus(Status.ACTIVE);

        List<CustomerAddress> list = new ArrayList<>();
        CustomerAddress address = new CustomerAddress();
        address.setAddressLine(customerSignupRequestDto.getPrimaryAddress().getAddressLine());
        address.setLatitude(customerSignupRequestDto.getPrimaryAddress().getLatitude());
        address.setLongitude(customerSignupRequestDto.getPrimaryAddress().getLongitude());
        address.setIsDefault(true);
        address.setUsers(user);

        list.add(address);
        user.setCustomerAddressList(list);

        Users savedUser = userRepository.save(user);

        //call method which send data to mail service through kafka for mailing
        sendMailForSignUp(savedUser.getRole(), savedUser.getUserName(), savedUser.getEmail(),"", savedUser.getCreatedAt());



        //Change to CustomerResponseDTO
        CustomerSignupResponseDto customerSignupResponseDto = new CustomerSignupResponseDto();
        customerSignupResponseDto.setId(savedUser.getId());
        customerSignupResponseDto.setUserName(savedUser.getUserName());
        customerSignupResponseDto.setEmail(savedUser.getEmail());
        customerSignupResponseDto.setPhoneNumber(savedUser.getPhoneNumber());
        customerSignupResponseDto.setRole(savedUser.getRole());
        customerSignupResponseDto.setStatus(savedUser.getStatus());

        CustomerAddressResponseDto customerAddressDto = new CustomerAddressResponseDto();
        customerAddressDto.setId(savedUser.getCustomerAddressList().get(0).getId());
        customerAddressDto.setAddressLine(savedUser.getCustomerAddressList().get(0).getAddressLine());
        customerAddressDto.setLatitude(savedUser.getCustomerAddressList().get(0).getLatitude());
        customerAddressDto.setLongitude(savedUser.getCustomerAddressList().get(0).getLongitude());
        customerAddressDto.setIsDefault(savedUser.getCustomerAddressList().get(0).getIsDefault());
        customerSignupResponseDto.setAddress(customerAddressDto);

        return customerSignupResponseDto;
    }
    @Override
    public RiderSignupResponseDto signupRider(RiderSignupRequestDto riderSignupRequestDto) {
        Users user = new Users();
        user.setUserName(riderSignupRequestDto.getUserName());
        user.setPassword(passwordEncoder.encode(riderSignupRequestDto.getPassword()));
        user.setEmail(riderSignupRequestDto.getEmail());
        user.setPhoneNumber(riderSignupRequestDto.getPhoneNumber());
        user.setRole(Role.RIDER);
        user.setStatus(Status.APPROVAL_PENDING);

        RiderDetails riderDetails = new RiderDetails();
        riderDetails.setVehicleType(riderSignupRequestDto.getRiderDetailsRequestDto().getVehicleType());
        riderDetails.setPermanentAddress(riderSignupRequestDto.getRiderDetailsRequestDto().getPermanentAddress());
        riderDetails.setLicensePlate(riderSignupRequestDto.getRiderDetailsRequestDto().getLicensePlate());
        riderDetails.setCurrentLatitude(riderSignupRequestDto.getRiderDetailsRequestDto().getCurrentLatitude());
        riderDetails.setCurrentLongitude(riderSignupRequestDto.getRiderDetailsRequestDto().getCurrentLongitude());
        riderDetails.setActiveStatus(riderSignupRequestDto.getRiderDetailsRequestDto().getActiveStatus());
        riderDetails.setUsers(user);

        user.setRiderDetails(riderDetails);
        Users savedUser = userRepository.save(user);

        //call method which send data to mail service through kafka for mailing
        sendMailForSignUp(savedUser.getRole(), savedUser.getUserName(), savedUser.getEmail(),"", savedUser.getCreatedAt());

        //change to Dto
        RiderSignupResponseDto responseDto = modelMapper.map(savedUser, RiderSignupResponseDto.class);
        if (savedUser.getRiderDetails() != null) {
            RiderDetailsResponseDto riderDetailsResponseDto = modelMapper.map(savedUser.getRiderDetails(), RiderDetailsResponseDto.class);
            responseDto.setRiderDetailsResponseDto(riderDetailsResponseDto);
        }
        return responseDto;
    }
    @Override
    public RestaurantSignupResponseDto signupRestaurant(RestaurantSignupRequestDto restaurantSignupRequestDto)
    {
        Users user=modelMapper.map(restaurantSignupRequestDto, Users.class);
        user.setPassword(passwordEncoder.encode(restaurantSignupRequestDto.getPassword()));
        user.setRole(Role.RESTAURANT_MANAGER);
        user.setStatus(Status.APPROVAL_PENDING);

        RestaurantDetails restaurantDetails =null;
        if(restaurantSignupRequestDto.getRestaurantDetailsRequestDto()!=null)
        {
            restaurantDetails =modelMapper.map(restaurantSignupRequestDto.getRestaurantDetailsRequestDto(), RestaurantDetails.class);
            restaurantDetails.setUsers(user);
        }
        user.setRestaurantDetails(restaurantDetails);
        Users savedUser=userRepository.save(user);

        //call method which send data to mail service through kafka for mailing
        sendMailForSignUp(savedUser.getRole(), savedUser.getUserName(), savedUser.getEmail(),
                savedUser.getRestaurantDetails().getRestaurantName(),savedUser.getCreatedAt());

        //change savedUser to RestaurantSignupResponseDto
        RestaurantSignupResponseDto restaurantSignupResponseDto=modelMapper.
                map(savedUser,RestaurantSignupResponseDto.class);
        if(savedUser.getRestaurantDetails()!=null)
        {
            RestaurantDetailsResponseDto restaurantDetailsResponseDto =modelMapper.
                    map(savedUser.getRestaurantDetails(), RestaurantDetailsResponseDto.class);
            restaurantSignupResponseDto.setRestaurantDetailsResponseDto(restaurantDetailsResponseDto);
        }
        return restaurantSignupResponseDto;
    }
    //send data to mail service through kafka for mailing
    public void sendMailForSignUp(Role role, String userName, String email, String restaurantName, LocalDateTime createdAt)
    {
        MailDto mailDto=new MailDto();
        mailDto.setUserName(userName);
        mailDto.setEmail(email);
        mailDto.setRestaurantName(restaurantName);
        mailDto.setCreationTime(createdAt);

        if(role==Role.CUSTOMER)
        {
            kafkaTemplate.send(customerKafkaTopic, email, mailDto);
            log.info("mail composing data for CUSTOMER sent to kafka topic: {}",customerKafkaTopic);
        }
        else if(role==Role.RIDER) {
            kafkaTemplate.send(riderKafkaTopic, email, mailDto);
            log.info("mail composing data for RIDER sent to kafka topic: {}",riderKafkaTopic);
        }
        else {
            kafkaTemplate.send(restaurantKafkaTopic, email, mailDto);
            log.info("mail composing data for RESTAURANT sent to kafka topic: {}",restaurantKafkaTopic);
        }
    }
    //send mail for password change
    //SignUpMailDto is null as we don't want to send it totally just need username
    public void sendMailForPassWordChange(String email,String userName)
    {
        MailDto mailDto=new MailDto();
        mailDto.setUserName(userName);
        mailDto.setEmail(email);
        kafkaTemplate.send(passwordChangeTopic,email,mailDto);
    }

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTAuthUtil jwtAuthUtil;
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto)
    {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserName(),
                        loginRequestDto.getPassword())
        );
        Users dbUser = userRepository.findUsersByUserName(loginRequestDto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtAuthUtil.generateJwtToken(dbUser.getUserName(), dbUser.getId().toString(), dbUser.getRole().name(),
                dbUser.getEmail(), dbUser.getPhoneNumber(), dbUser.getStatus().name());
        return new LoginResponseDto(dbUser.getId(), token);
    }

    @Override
    public UserProfileResponseDto fetchLoggedInUserProfile()
    {
        Users currentUser = userRepository.findUsersByUserName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponseDto userProfileResponseDto=modelMapper.
                map(currentUser,UserProfileResponseDto.class);
        if(currentUser.getRole()==Role.CUSTOMER)
        {
          List<CustomerProfileResponseDto> list=new ArrayList<>();
          for(CustomerAddress address:currentUser.getCustomerAddressList())
          {
              list.add(modelMapper.
                      map(address, CustomerProfileResponseDto.class));
          }
          userProfileResponseDto.setCustomerProfileResponseDtoList(list);
        }
        else if(currentUser.getRole()==Role.RIDER)
        {
           RiderProfileResponseDto riderProfileResponseDto=modelMapper.
                   map(currentUser.getRiderDetails(), RiderProfileResponseDto.class);
           userProfileResponseDto.setRiderProfileResponseDto(riderProfileResponseDto);
        }
        else if(currentUser.getRole()==Role.RESTAURANT_MANAGER)
        {
            RestaurantProfileResponseDto restaurantProfileResponseDto=modelMapper.
                    map(currentUser.getRestaurantDetails(), RestaurantProfileResponseDto.class);
            userProfileResponseDto.setRestaurantProfileResponseDto(restaurantProfileResponseDto);
        }
        else {
            //admin not needed only one admin
        }
        return userProfileResponseDto;
    }


    @Override
    public UpdateUserResponseDto updateLoggedInUser(UpdateUserRequestDto updateUserRequestDto)
    {

        Users currentUser = userRepository.findUsersByUserName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //set common fields from dto
        currentUser.setUserName(updateUserRequestDto.getUserName());
        currentUser.setEmail(updateUserRequestDto.getEmail());
        currentUser.setPhoneNumber(updateUserRequestDto.getPhoneNumber());
        currentUser.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));

        if(currentUser.getRole()==Role.RIDER)
        {
           if(updateUserRequestDto.getUpdateRiderRequestDto()==null)
               throw new RuntimeException("Mismatch alert:Should have been update for Rider");
           currentUser.getRiderDetails().setCurrentLatitude(updateUserRequestDto.getUpdateRiderRequestDto().getCurrentLatitude());
           currentUser.getRiderDetails().setPermanentAddress(updateUserRequestDto.getUpdateRiderRequestDto().getPermanentAddress());
           currentUser.getRiderDetails().setCurrentLongitude(updateUserRequestDto.getUpdateRiderRequestDto().getCurrentLongitude());
           currentUser.getRiderDetails().setLicensePlate(updateUserRequestDto.getUpdateRiderRequestDto().getLicensePlate());
           currentUser.getRiderDetails().setVehicleType(updateUserRequestDto.getUpdateRiderRequestDto().getVehicleType());
           currentUser.getRiderDetails().setActiveStatus(updateUserRequestDto.getUpdateRiderRequestDto().getActiveStatus());
           Users savedUser=userRepository.save(currentUser);
            //sending response by changing to responseDto
            UpdateUserResponseDto updateUserResponseDto=modelMapper.map(savedUser,UpdateUserResponseDto.class);
             if(savedUser.getRiderDetails()!=null)
             {
                 UpdateRiderResponseDto updateRiderResponseDto=modelMapper.
                         map(savedUser.getRiderDetails(),UpdateRiderResponseDto.class);
                 updateUserResponseDto.setUpdateRiderResponseDto(updateRiderResponseDto);
             }
             return updateUserResponseDto;
        }
        else if(currentUser.getRole()==Role.RESTAURANT_MANAGER)
        {
             if(updateUserRequestDto.getUpdateRestaurantRequestDto()==null)
                 throw new RuntimeException("Mismatch alert:Should have been update for Restaurant");

             currentUser.getRestaurantDetails().setBusinessLicenseNumber(updateUserRequestDto.getUpdateRestaurantRequestDto().getBusinessLicenseNumber());
             currentUser.getRestaurantDetails().setRestaurantName(updateUserRequestDto.getUpdateRestaurantRequestDto().getRestaurantName());
             currentUser.getRestaurantDetails().setRestaurantAddress(updateUserRequestDto.getUpdateRestaurantRequestDto().getRestaurantAddress());
             currentUser.getRestaurantDetails().setLatitude(updateUserRequestDto.getUpdateRestaurantRequestDto().getLatitude());
             currentUser.getRestaurantDetails().setLongitude(updateUserRequestDto.getUpdateRestaurantRequestDto().getLongitude());
             currentUser.getRestaurantDetails().setWorkingHours(updateUserRequestDto.getUpdateRestaurantRequestDto().getWorkingHours());

             Users savedUser=userRepository.save(currentUser);

             //change to dto and send response
            UpdateUserResponseDto updateUserResponseDto=modelMapper.map(savedUser,UpdateUserResponseDto.class);
            if(savedUser.getRestaurantDetails()!=null)
            {
                UpdateRestaurantResponseDto updateRestaurantResponseDto=modelMapper.
                        map(savedUser.getRestaurantDetails(), UpdateRestaurantResponseDto.class);
                updateUserResponseDto.setUpdateRestaurantResponseDto(updateRestaurantResponseDto);
            }
           return updateUserResponseDto;
        }
        else if(currentUser.getRole()==Role.CUSTOMER)
        {
          if(updateUserRequestDto.getUpdateCustomerRequestDtoList()==null)
              throw new RuntimeException("Mismatch alert:Should have been update for Customer");

          //list coming for as dto Customer address should be equal to size of list present in DB
            if(updateUserRequestDto.getUpdateCustomerRequestDtoList().size()!=
                    currentUser.getCustomerAddressList().size())
                throw new RuntimeException("Number of address present  in DB should be equal to address being sent as Dto");
            //check if Dto contains more than one address with isDefault true
            int count=0;
            for(UpdateCustomerRequestDto dto: updateUserRequestDto.getUpdateCustomerRequestDtoList())
            {
                if(dto.getIsDefault()==true)
                    count++;
            }
            if(count>1)
                throw new RuntimeException("More than one address cannot be default, check and select default carefully please");

          for(UpdateCustomerRequestDto updateDto:updateUserRequestDto.getUpdateCustomerRequestDtoList())
          {   //map to entity
              //CustomerAddress customer=modelMapper.map(updateDto,CustomerAddress.class);
              CustomerAddress customer=new CustomerAddress();
              customer.setId(updateDto.getId());
              customer.setLongitude(updateDto.getLongitude());
              customer.setLatitude(updateDto.getLatitude());
              customer.setIsDefault(updateDto.getIsDefault());
              int length=currentUser.getCustomerAddressList().size();
              for(int i=0;i<length;i++)
              {
                 if(currentUser.getCustomerAddressList().get(i).getId()==
                         customer.getId())
                 {
                     currentUser.getCustomerAddressList().get(i).setLongitude(customer.getLongitude());
                     currentUser.getCustomerAddressList().get(i).setLatitude(customer.getLatitude());
                     currentUser.getCustomerAddressList().get(i).setAddressLine(customer.getAddressLine());
                     currentUser.getCustomerAddressList().get(i).setIsDefault(customer.getIsDefault());

                 }
              }

          }
          Users savedUser=userRepository.save(currentUser);
          UpdateUserResponseDto responseDto=new UpdateUserResponseDto();
          responseDto.setId(savedUser.getId());
          responseDto.setUserName(savedUser.getUserName());
          responseDto.setEmail(savedUser.getEmail());
          responseDto.setPhoneNumber(savedUser.getPhoneNumber());
          responseDto.setCreatedAt(savedUser.getCreatedAt());
          responseDto.setUpdatedAt(savedUser.getUpdatedAt());
            List<UpdateCustomerResponseDto> updateCustomerResponseDto=
                    new ArrayList<>();
           for(CustomerAddress customerAddress:savedUser.getCustomerAddressList())
           {
               UpdateCustomerResponseDto updateDto=new UpdateCustomerResponseDto();
               updateDto.setId(customerAddress.getId());
               updateDto.setAddressLine(customerAddress.getAddressLine());
               updateDto.setLongitude(customerAddress.getLongitude());
               updateDto.setLatitude(customerAddress.getLatitude());
               updateDto.setCreatedAt(customerAddress.getCreatedAt());
               updateDto.setUpdatedAt(customerAddress.getUpdatedAt());
               updateDto.setIsDefault(customerAddress.getIsDefault());
               updateCustomerResponseDto.add(updateDto);
           }
          responseDto.setUpdateCustomerResponseDtoList(updateCustomerResponseDto);
           return responseDto;
        }
        else
        {
            //for admin no need,only one admin is there
        }
        return null;
    }

    @Override
    public String updatePasswordForLoggedInUser(String password)
    {
        Users currentUser = userRepository.findUsersByUserName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .orElseThrow(() -> new RuntimeException("User not found"));
        currentUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(currentUser);
        sendMailForPassWordChange(currentUser.getEmail(),currentUser.getUserName());
        return "your password has been changed successfully";

    }



    //only for admin methods
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String changeStatusOfUserByUUID(UUID id, Status status)
    {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No element with given id present"));
        Status status1 = user.getStatus();
        user.setStatus(status);
        userRepository.save(user);
        return "Status for user with id: " + id + " changed from : " + status1 + " to " + status;
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserByUUID(UUID id)
    {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No element with given id present"));
        String userName = user.getUserName();
        userRepository.deleteById(id);
        return "User with id: " + id + " and userName : " + userName + " deleted";
    }


}
