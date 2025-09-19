package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("arslanpk997@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send email");
            e.printStackTrace();
        }
    }
    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
try {


    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Password Reset Request");
    message.setText("Click the link to reset your password: " + resetUrl);
    message.setFrom("arslanpk997@gmail.com");
    mailSender.send(message);
    System.out.println("✅ Email sent to " + to);
}catch (Exception e) {
    System.out.println("❌ Failed to send email");
    e.printStackTrace();
}
    }


    public void sendOrderEmail(String to, Long orderId, OrderStatus orderStatus, double totalAmount) {
        try {
            String subject = "Order #" + orderId + " - " + orderStatus;
            String body = "Hello,\n\n" +
                    "Your order with ID #" + orderId + " has been updated.\n" +
                    "Current Status: " + orderStatus + "\n" +
                    "Total Amount: ₹" + totalAmount + "\n\n" +
                    "Thank you for shopping with us!\n\n" +
                    "Best regards,\n" +
                    "E-Commerce Team";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("arslanpk997@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Order email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send order email");
            e.printStackTrace();
        }
    }



    // Optional: add a method for HTML email later
}

