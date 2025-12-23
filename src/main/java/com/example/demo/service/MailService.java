package com.example.demo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetEmail(String to, String token) {
        String subject = "AlephLearn Password Reset";
        String text = String.format("""
                Hello,

                You requested to reset your password for your AlephLearn account.

                Your reset token is: %s

                Or click this link to reset directly:
                http://localhost:5500/Fronted/Auth.html#reset?token=%s

                If you didn’t request this, please ignore this email.

                — AlephLearn Team
                """, token, token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}