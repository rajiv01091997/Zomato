package com.zomato.mail_service.service;

import com.zomato.mail_service.dto.SignUpMailDto;
import com.zomato.mail_service.entity.RestaurantMail;
import com.zomato.mail_service.repository.RestaurantRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MailService implements MailServiceInterface
{
    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Autowired
    private RestaurantRepository restaurantRepository;
    @KafkaListener(topics = "${restaurant.topic.name}")
    public void sendToRestaurantForSignUp(ConsumerRecord<String, SignUpMailDto> record)
    {
        try {
             SignUpMailDto signUpMailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject="Welcome onboard: "+signUpMailDto.getRestaurantName().toUpperCase()+"!!!!!";

            String emailBody = "<div style='position:relative; padding-bottom:100px;'>" +
                    "<p>Hello " + signUpMailDto.getUserName() + ",</p>" +
                    "<p>Welcome to NutriMatrix!</p>" +
                    "<p>An account for your restaurant "+ signUpMailDto.getRestaurantName().toUpperCase() + " has been created successfully...</p>" +
                    "<p>Please hold tight till we verify your account and then follow the steps below:</p>" +
                    "<ol>" +
                    "<li>Log in to your dashboard</li>" +
                    "<li>Complete your restaurant profile</li>" +
                    "<li>Upload your menu items</li>" +
                    "<li>Set your delivery zones</li>" +
                    "<li>Configure payment methods</li>" +
                    "<li>Go live and start receiving orders</li>" +
                    "</ol>" +
                    "<p>We're excited to have you on board!</p>" +
                    "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed</p>" +
                    "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +
                    // Logo image positioned bottom-left within parent container
                    "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                    "</div>";


            helper.setFrom(fromEmail);
            helper.setTo(signUpMailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody,true);

            // Use FileSystemResource for local file access
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

            // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
            helper.addInline("nutrimatrixLogo", logo, "image/png");

//            if (details.getAttachment() != null && details.getAttachment().length > 0) {
//                helper.addAttachment(details.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(details.getAttachment()));
//            }
            mailSender.send(message);
            System.out.println("Email sent successfully to " + signUpMailDto.getEmail() + " at " + new java.util.Date());

            RestaurantMail mail=new RestaurantMail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(signUpMailDto.getUserName());
            mail.setTo(signUpMailDto.getEmail());
            restaurantRepository.save(mail);

        }
        catch(MessagingException e)
        {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${rider.topic.name}")
    public void sendToRiderForSignUp(ConsumerRecord<String, SignUpMailDto> record) {
        try {
            SignUpMailDto signUpMailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Welcome onboard: " + signUpMailDto.getUserName().toUpperCase() + " - NutriMatrix Rider!";

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello " + signUpMailDto.getUserName() + ",</p>" +
                            "<p>Welcome to NutriMatrix!</p>" +
                            "<p>An account for you as a delivery rider has been created successfully. You are now part of our fast and reliable delivery team.</p>" +
                            "<p>Once your account is verified, you can log in and start accepting delivery tasks. Stay tuned for instructions from your dashboard.</p>" +
                            "<p>Your responsibilities as a NutriMatrix Rider:</p>" +
                            "<ol>" +
                            "<li>Check your assigned orders in the app</li>" +
                            "<li>Pick up food from restaurants promptly</li>" +
                            "<li>Ensure on-time and safe delivery to customers</li>" +
                            "<li>Update delivery status after each order</li>" +
                            "<li>Contact support if you face any issues</li>" +
                            "</ol>" +
                            "<p>Thank you for joining us! We’re excited to have you onboard.</p>" +
                            "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed</p>" +
                            "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +
                            "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                            "</div>";


            helper.setFrom(fromEmail);
            helper.setTo(signUpMailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Use FileSystemResource for local file access
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

            // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
            helper.addInline("nutrimatrixLogo", logo, "image/png");

//            if (details.getAttachment() != null && details.getAttachment().length > 0) {
//                helper.addAttachment(details.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(details.getAttachment()));
//            }
            mailSender.send(message);
            System.out.println("Email sent successfully to " + signUpMailDto.getEmail() + " at " + new java.util.Date());

            RestaurantMail mail = new RestaurantMail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(signUpMailDto.getUserName());
            mail.setTo(signUpMailDto.getEmail());
            restaurantRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
        @KafkaListener(topics = "${customer.topic.name}")
        public void sendToCustomerForSignUp(ConsumerRecord<String, SignUpMailDto> record)
        {
            try {
                SignUpMailDto signUpMailDto = record.value();
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                String subject = "Welcome to NutriMatrix, " + signUpMailDto.getUserName() + "!";

                String emailBody =
                        "<div style='position:relative; padding-bottom:100px;'>" +
                                "<p>Hello " + signUpMailDto.getUserName() + ",</p>" +
                                "<p>Thank you for signing up with NutriMatrix!</p>" +
                                "<p>Your customer account has been created successfully. You can now order delicious food from your favorite restaurants and get it delivered at lightning speed.</p>" +
                                "<p>Getting started is easy:</p>" +
                                "<ol>" +
                                "<li>Log in to your NutriMatrix account</li>" +
                                "<li>Browse restaurants and menu items</li>" +
                                "<li>Add your favorite food to your cart</li>" +
                                "<li>Go through a quick and secure checkout</li>" +
                                "<li>Track your order in real time</li>" +
                                "<li>Enjoy fast and reliable delivery!</li>" +
                                "</ol>" +
                                "<p>Your satisfaction is our top priority. If you need any assistance, our support team is here to help.</p>" +
                                "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed</p>" +
                                "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +
                                "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                                "</div>";

                helper.setFrom(fromEmail);
                helper.setTo(signUpMailDto.getEmail());
                helper.setSubject(subject);
                helper.setText(emailBody,true);

                // Use FileSystemResource for local file access
                FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

                // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
                helper.addInline("nutrimatrixLogo", logo, "image/png");

//            if (details.getAttachment() != null && details.getAttachment().length > 0) {
//                helper.addAttachment(details.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(details.getAttachment()));
//            }
                mailSender.send(message);
                System.out.println("Email sent successfully to " + signUpMailDto.getEmail() + " at " + new java.util.Date());

                RestaurantMail mail=new RestaurantMail();
                mail.setSubject(subject);
                mail.setBody(emailBody);
                mail.setUserName(signUpMailDto.getUserName());
                mail.setTo(signUpMailDto.getEmail());
                restaurantRepository.save(mail);

            }
            catch(MessagingException e)
            {
                System.err.println("Failed to send email: " + e.getMessage());
            }
    }
    @KafkaListener(topics = "${password.change.topic.name}")
    public void forPasswordChange(ConsumerRecord<String,SignUpMailDto> record)
    {
        try {
            SignUpMailDto signUpMailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Password change Alert!!!!" ;

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px; font-family:Arial, sans-serif;'>" +
                    "<p>Hello " + signUpMailDto.getUserName() + ",</p>" +
                    "<p>Your NutriMatrix account password has been successfully updated.</p>" +
                    "<p>If you made this change, no further action is required.</p>" +
                    "<p style='margin-top:20px;'>However, if you did <strong>not</strong> request this password change, please:</p>" +
                    "<ol>" +
                    "<li>Reset your password immediately from the NutriMatrix login page</li>" +
                    "<li>Check your account activity for any unauthorized access</li>" +
                    "<li>Contact NutriMatrix Support for quick help</li>" +
                    "</ol>" +
                    "<p>Your account security is extremely important to us. We are always here to ensure your safety and comfort.</p>" +
                    "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed</p>" +
                    "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +
                    "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                    "</div>";


            helper.setFrom(fromEmail);
            helper.setTo(signUpMailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody,true);

            // Use FileSystemResource for local file access
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

            // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
            helper.addInline("nutrimatrixLogo", logo, "image/png");

//            if (details.getAttachment() != null && details.getAttachment().length > 0) {
//                helper.addAttachment(details.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(details.getAttachment()));
//            }
            mailSender.send(message);
            System.out.println("Email sent successfully to " + signUpMailDto.getEmail() + " at " + new java.util.Date());

            RestaurantMail mail=new RestaurantMail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(signUpMailDto.getUserName());
            mail.setTo(signUpMailDto.getEmail());
            restaurantRepository.save(mail);

        }
        catch(MessagingException e)
        {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    }

