package com.hostel.smart_hostel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String subject, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Dear Parent,\n\nThe One-Time Password (OTP) for your child's leave request is: " + otp + "\n\nPlease use this code to approve the request.\n\nThank you,\nSRM Hostel Administration");
        mailSender.send(message);
    }
}