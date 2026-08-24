package com.lastmile.tracker.service;

import com.lastmile.tracker.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    @Value("${MAIL_FROM:no-reply@lastmile.local}")
    private String from;

    public void send(User recipient, String title, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(recipient.getEmail());
            mail.setSubject(title);
            mail.setText(message);
            mailSender.send(mail);
        } catch (RuntimeException exception) {
            log.warn("Unable to send notification email to {}", recipient.getEmail(), exception);
        }
    }
}