package com.chak.E_Commerce_Back_End.service;

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
            message.setFrom("your_email@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send email");
            e.printStackTrace();
        }
    }


    // Optional: add a method for HTML email later
}

