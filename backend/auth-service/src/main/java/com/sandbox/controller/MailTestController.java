package com.sandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sandbox.mail.HtmlEmailBuilder;
import com.sandbox.service.EmailService;

import lombok.RequiredArgsConstructor;

/*
 * TEMPORARY CONTROLLER
 *
 * Used only for testing email functionality.
 *
 * Delete after development.
 */

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final EmailService emailService;

    private final HtmlEmailBuilder htmlEmailBuilder;

    /*
     * Test Email
     */
    @GetMapping("/mail/test")
    public String testEmail() {

        String html = htmlEmailBuilder.buildOtpTemplate(

                "Maroof",

                "482951",

                10

        );

        emailService.sendEmail(

                "maroofproject1@gmail.com",

                "Sandbox OTP Test",

                html

        );

        return "Email Sent Successfully.";

    }

}