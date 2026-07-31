package com.sandbox.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sandbox.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

/*
 * EMAIL SERVICE IMPLEMENTATION
 *
 * Sends HTML emails using Spring Mail.
 */

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // Spring email sender
    private final JavaMailSender mailSender;

    /*
     * Sender email configured in application.properties
     */
    @org.springframework.beans.factory.annotation.Value("${app.mail.from}")
    private String fromEmail;

    /*
     * Sends HTML email.
     */
    @Override
    public void sendEmail(

            String to,

            String subject,

            String html

    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =

                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(html, true);

            mailSender.send(message);

        }

        catch (Exception ex) {

            throw new RuntimeException(

                    "Unable to send email.",

                    ex

            );

        }

    }

}