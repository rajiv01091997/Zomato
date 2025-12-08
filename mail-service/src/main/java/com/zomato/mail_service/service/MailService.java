package com.zomato.mail_service.service;

import com.zomato.mail_service.dto.MailDto;
import com.zomato.mail_service.entity.Mail;
import com.zomato.mail_service.feign.UserServiceClient;
import com.zomato.mail_service.repository.MailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @Autowired
    private UserServiceClient userServiceClient;

    @Value("${email.from}")
    private String fromEmail;

    @Autowired
    private MailRepository mailRepository;
    @KafkaListener(topics = "${restaurant.signup.topic}")
    public void sendToRestaurantForSignUp(ConsumerRecord<String, MailDto> record)
    {
        try {
             MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject="Welcome onboard: "+mailDto.getRestaurantName().toUpperCase()+"!!!!!";

            String emailBody = "<div style='position:relative; padding-bottom:100px;'>" +
                    "<p>Hello " + mailDto.getUserName() + ",</p>" +
                    "<p>Welcome to NutriMatrix!</p>" +
                    "<p>An account for your restaurant "+ mailDto.getRestaurantName().toUpperCase() + " has been created successfully...</p>" +
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
            helper.setTo(mailDto.getEmail());
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
            System.out.println("Email sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

            Mail mail=new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mailRepository.save(mail);

        }
        catch(MessagingException e)
        {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${rider.signup.topic}")
    public void sendToRiderForSignUp(ConsumerRecord<String, MailDto> record) {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Welcome onboard: " + mailDto.getUserName().toUpperCase() + " - NutriMatrix Rider!";

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello " + mailDto.getUserName() + ",</p>" +
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
            helper.setTo(mailDto.getEmail());
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
            System.out.println("Email sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mailRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
        @KafkaListener(topics = "${customer.signup.topic}")
        public void sendToCustomerForSignUp(ConsumerRecord<String, MailDto> record)
        {
            try {
                MailDto mailDto = record.value();
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                String subject = "Welcome to NutriMatrix, " + mailDto.getUserName() + "!";

                String emailBody =
                        "<div style='position:relative; padding-bottom:100px;'>" +
                                "<p>Hello " + mailDto.getUserName() + ",</p>" +
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
                helper.setTo(mailDto.getEmail());
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
                System.out.println("Email sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

                Mail mail=new Mail();
                mail.setSubject(subject);
                mail.setBody(emailBody);
                mail.setUserName(mailDto.getUserName());
                mail.setTo(mailDto.getEmail());
                mailRepository.save(mail);

            }
            catch(MessagingException e)
            {
                System.err.println("Failed to send email: " + e.getMessage());
            }
    }
    @KafkaListener(topics = "${password.change.topic}")
    public void forPasswordChange(ConsumerRecord<String, MailDto> record)
    {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Password change Alert!!!!" ;

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px; font-family:Arial, sans-serif;'>" +
                    "<p>Hello " + mailDto.getUserName() + ",</p>" +
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
            helper.setTo(mailDto.getEmail());
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
            System.out.println("Email sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

            Mail mail=new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mailRepository.save(mail);

        }
        catch(MessagingException e)
        {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }


    @KafkaListener(topics = "${customer.acknowledgement.topic}")
    public void sendOrderAcknowledgementWithInvoice(ConsumerRecord<String, MailDto> record) {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Order Confirmed: #" + mailDto.getOrderId() + " - NutriMatrix";


            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello " + mailDto.getUserName() + ",</p>" +
                            "<p>Thank you for ordering from <strong>NutriMatrix</strong>! 🎉 Your order has been placed successfully.</p>" +

                            "<h3>📦 Order Summary</h3>" +
                            "<p><strong>Order ID:</strong> " + mailDto.getOrderId() + "</p>" +
                            "<p><strong>Order Time:</strong> " + mailDto.getCreationTime() + "</p>" +

                            "<p>Your items are now being prepared by the restaurant. You will receive updates as your order moves to pickup and delivery stages.</p>" +

                            "<h3>🧾 Invoice Attached</h3>" +
                            "<p>Your detailed invoice is attached to this email for your reference.</p>" +

                            "<h3>🛵 What Happens Next?</h3>" +
                            "<ol>" +
                            "<li>The restaurant confirms your order.</li>" +
                            "<li>A delivery rider is assigned to your order.</li>" +
                            "<li>The rider picks up your food and delivers it to your address.</li>" +
                            "</ol>" +

                            "<p>If you have any questions or face any issues, feel free to contact our support team.</p>" +

                            "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed ⚡</p>" +

                            "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +

                            "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                            "</div>";


            helper.setFrom(fromEmail);
            helper.setTo(mailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Use FileSystemResource for local file access
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

            // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
            helper.addInline("nutrimatrixLogo", logo, "image/png");

            if (mailDto.getAttachment() != null && mailDto.getAttachment().length > 0) {
                helper.addAttachment(mailDto.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(mailDto.getAttachment()));
            }
            mailSender.send(message);
            System.out.println("Email sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

           Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mail.setAttachmentName(mailDto.getAttachmentName());
            mail.setAttachment(mailDto.getAttachment());
            mail.setOrderId(mailDto.getOrderId());
            mailRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    @KafkaListener(topics="${restaurant.acknowledgement.topic}")
    public void notifyRestaurant(ConsumerRecord<String,MailDto> record)
    {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "🆕 New Order #" + mailDto.getOrderId() + " - NutriMatrix";


            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello <strong>" + mailDto.getRestaurantName() + "</strong>,</p>" +
                            "<p>You have received a <strong>new order</strong> from NutriMatrix! 🎉</p>" +

                            "<h3>📦 Order Summary</h3>" +
                            "<p><strong>Order ID:</strong> " + mailDto.getOrderId() + "</p>" +
                            "<p><strong>Order Time:</strong> " + mailDto.getCreationTime() + "</p>" +

                            "<h3>🛵 What Happens Next?</h3>" +
                            "<ol>" +
                            "<li><strong>Mandatory first step:Change status to CONFIRMED and start cooking</strong></li>" +
                            "<li><strong>Optional:Change status to PREPARING</strong></li>" +
                            "<li><strong>Mandatory second step: Change status to READY_TO_PICKUP</strong> (when packed & ready)</li>" +
                            "</ol>" +

                            "<h3>🧾 Invoice Attached</h3>" +
                            "<p>Complete invoice with <strong>items list, customer details & payment info</strong> attached.</p>" +

                            "<p>Check invoice for exact items to prepare!</p>" +

                            "<p>If you have any questions, contact support immediately.</p>" +

                            "<p>Best regards,<br>The NutriMatrix Team<br>Food at Lightning Speed ⚡</p>" +

                            "<p>SUPPORT:<br>Email: nutrimatrix@zohomail.in<br>Phone: +91 6398109021</p>" +

                            "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                            "</div>";


            helper.setFrom(fromEmail);
            helper.setTo(mailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Use FileSystemResource for local file access
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));

            // Provide explicit image type if needed (e.g. "image/png" or "image/jpeg")
            helper.addInline("nutrimatrixLogo", logo, "image/png");

            if (mailDto.getAttachment() != null && mailDto.getAttachment().length > 0) {
                helper.addAttachment(mailDto.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(mailDto.getAttachment()));
            }
            mailSender.send(message);
            System.out.println("Email notification sent successfully to restaurant on: " + mailDto.getEmail() + " at " + new java.util.Date());

            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mail.setAttachmentName(mailDto.getAttachmentName());
            mail.setAttachment(mailDto.getAttachment());
            mailRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send notification to restaurant: " + e.getMessage());
        }
    }
    @KafkaListener(topics="${rider.acknowledgement.topic}")
    public void notifyRider(ConsumerRecord<String,MailDto> record)
    {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "📦 Order Assigned #" + mailDto.getOrderId() + " - Pickup Ready!";

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello ,"+mailDto.getUserName()+"</p>" +
                            "<p>You have been <strong>assigned a new delivery</strong>! 🎉</p>" +

                            "<div style='background: #e8f5e8; padding: 20px; border-radius: 10px; border-left: 5px solid #27ae60;'>" +
                            "<h4 style='color: #27ae60;'>🔐 OTP for Restaurant Verification</h4>" +
                            "<p style='font-size: 24px; font-weight: bold; color: #e74c3c; letter-spacing: 5px;'>" +
                            mailDto.getExtraInfo() +
                            "</p>" +
                            "<p><strong>Share this OTP with restaurant staff to verify identity!</strong></p>" +
                            "</div>" +

                            "<h3>📍 Delivery and pickup details can be found in invoice attached</h3>" +
                            "<p><strong>Order ID:</strong> " + mailDto.getOrderId() + "</p>" +
                            "<p><strong>Restaurant:</strong> " + mailDto.getRestaurantName() + "</p>" +


                            "<h3>🚀 Steps to Follow:</h3>" +
                            "<ol>" +
                            "<li><strong>Mandatory First: Get OTP Verified at restaurant & mark PICKED_UP and proceed for delivery</strong></li>" +
                            "<li>Optional: Change status to OUT_FOR_DELIVERY</li>" +
                            "<li><strong>Mandatory second: Change status to DELIVERED</strong> → Mark DELIVERED at customer door</li>" +
                            "</ol>" +

                            "<div style='background: #fff3cd; padding: 20px; border-radius: 10px; border-left: 5px solid #f39c12; margin: 20px 0;'>" +
                            "<h4 style='color: #856404;'>💰 Order Already Paid</h4>" +
                            "<p style='margin: 5px 0; font-weight: bold;'>No payment required at delivery!</p>" +
                            "</div>" +

                            "<h3>📞 Need Help?</h3>" +
                            "<p>Contact us immediately:</p>" +
                            "<p style='font-size: 18px; font-weight: bold; color: #e74c3c;'>" +
                            "📞 +91 6398109021 | ✉️ nutrimatrix@zohomail.in" +
                            "</p>" +


                            "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                            "</div>";

            helper.setFrom(fromEmail);
            helper.setTo(mailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Logo
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));
            helper.addInline("nutrimatrixLogo", logo, "image/png");

            if (mailDto.getAttachment() != null && mailDto.getAttachment().length > 0) {
                helper.addAttachment(mailDto.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(mailDto.getAttachment()));
            }

            mailSender.send(message);
            System.out.println("Rider notification sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mail.setAttachmentName(mailDto.getAttachmentName());
            mail.setAttachment(mailDto.getAttachment());
            mailRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send notification to rider: " + e.getMessage());
        }

    }
    @KafkaListener(topics="${delivery.confirmation.topic}")
    public void deliveryConfirmation(ConsumerRecord<String,MailDto> record)
    {
        try {
            MailDto mailDto = record.value();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "✅ Order Delivered! #" + mailDto.getOrderId() + " - NutriMatrix";

            String emailBody =
                    "<div style='position:relative; padding-bottom:100px;'>" +
                            "<p>Hello " + mailDto.getUserName() + ",</p>" +
                            "<h2 style='color: #27ae60; text-align: center;'>🎉 Your Order has been Delivered!</h2>" +
                            "<p>Thank you for choosing <strong>NutriMatrix</strong>! Your food has been safely delivered.</p>" +

                            "<h3>📦 Order Summary</h3>" +
                            "<p><strong>Order ID:</strong> " + mailDto.getOrderId() + "</p>" +
                            "<p><strong>Delivery Time:</strong> " + mailDto.getCreationTime() + "</p>" +

                            "<div style='background: #e8f5e8; padding: 25px; border-radius: 10px; border-left: 5px solid #27ae60; text-align: center;'>" +
                            "<h3 style='color: #27ae60; margin-top: 0;'>✅ Delivery Complete!</h3>" +
                            "<p style='font-size: 18px; margin: 10px 0;'>Your order from <strong>" + mailDto.getRestaurantName() + "</strong></p>" +
                            "<p style='font-size: 20px; color: #e74c3c; font-weight: bold;'>has been delivered successfully!</p>" +
                            "</div>" +

                            "<h3>🧾 Invoice Attached</h3>" +
                            "<p>Your complete invoice with all items, payment details & order history is attached.</p>" +

                            "<h3>⭐⭐⭐⭐⭐ Rate Your Experience</h3>" +
                            "<p>Please rate the restaurant and rider within next 24 hours:</p>" +
                            "<ul style='background: #f8f9fa; padding: 15px; border-radius: 8px;'>" +
                            "<li><strong>Restaurant:</strong> Food quality & packaging</li>" +
                            "<li><strong>Rider:</strong> Delivery speed & politeness</li>" +
                            "</ul>" +
                            "<p><em>Link will be sent separately</em></p>" +

                            "<h3>📞 Need Help?</h3>" +
                            "<p>Any issues with your order? Contact us immediately:</p>" +
                            "<p style='font-size: 18px; font-weight: bold; color: #e74c3c;'>" +
                            "📞 +91 6398109021 | ✉️ nutrimatrix@zohomail.in" +
                            "</p>" +

                            "<p style='text-align: center; margin-top: 30px;'>" +
                            "Best regards,<br>" +
                            "<strong>The NutriMatrix Team</strong><br>" +
                            "Food at Lightning Speed ⚡" +
                            "</p>" +

                            "<img src='cid:nutrimatrixLogo' style='position:absolute; bottom:0; left:0; width:150px;' alt='NutriMatrix Logo'/>" +
                            "</div>";

            helper.setFrom(fromEmail);
            helper.setTo(mailDto.getEmail());
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Logo
            FileSystemResource logo = new FileSystemResource(new File("/Users/rajivyadav/Desktop/Applogo.png"));
            helper.addInline("nutrimatrixLogo", logo, "image/png");

            if (mailDto.getAttachment() != null && mailDto.getAttachment().length > 0) {
                helper.addAttachment(mailDto.getAttachmentName(), new org.springframework.core.io.ByteArrayResource(mailDto.getAttachment()));
            }

            mailSender.send(message);
            System.out.println("delivery notification sent successfully to " + mailDto.getEmail() + " at " + new java.util.Date());

            //get invoice from DB to send here
            Mail DbMail=mailRepository.findByOrderId(mailDto.getOrderId()).orElseThrow(()->new RuntimeException("No Document with this orderId present in DB"));
            String attachmentName=DbMail.getAttachmentName();
            byte[] attachment=DbMail.getAttachment();

            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(emailBody);
            mail.setUserName(mailDto.getUserName());
            mail.setTo(mailDto.getEmail());
            mail.setAttachmentName(attachmentName);
            mail.setAttachment(attachment);
            mailRepository.save(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send delivery notification to customer: " + e.getMessage());
        }

    }

    }

