package com.sandbox.mail;

import org.springframework.stereotype.Component;

/**
 * Builds reusable HTML email templates used across the application.
 */
@Component
public class HtmlEmailBuilder {

	/**
	 * Returns the requested email template.
	 */
	public String buildEmail(EmailTemplateType template, String title, String message, String buttonText,
			String buttonUrl) {

		return switch (template) {

		case EMAIL_VERIFICATION -> buildVerificationTemplate(title, message, buttonText, buttonUrl);

		case PASSWORD_RESET_OTP -> buildOtpTemplate(title, message);

		case PASSWORD_CHANGED -> buildSimpleTemplate(title, message);

		case WELCOME -> buildSimpleTemplate(title, message);

		case SECURITY_ALERT -> buildSimpleTemplate(title, message);
		};
	}

	/**
	 * Email verification template.
	 */
	private String buildVerificationTemplate(String title, String message, String buttonText, String buttonUrl) {

		return """
				<!DOCTYPE html>

				<html>

				<body style="
				        font-family:Arial;
				        background:#f5f7fb;
				        padding:40px;">

				    <div style="
				            max-width:650px;
				            margin:auto;
				            background:white;
				            border-radius:12px;
				            overflow:hidden;
				            box-shadow:0 3px 10px rgba(0,0,0,.1);">

				        <div style="
				                background:#2563eb;
				                color:white;
				                padding:25px;
				                text-align:center;">

				            <h2>SandBox</h2>

				        </div>

				        <div style="padding:40px;">

				            <h2>%s</h2>

				            <p>%s</p>

				            <br>

				            <a href="%s"
				               style="
				               background:#2563eb;
				               color:white;
				               text-decoration:none;
				               padding:15px 30px;
				               border-radius:8px;
				               display:inline-block;">

				                %s

				            </a>

				            <br><br>

				            <p style="color:gray">

				                If the button doesn't work,
				                copy the link into your browser.

				            </p>

				            <p>%s</p>

				        </div>

				    </div>

				</body>

				</html>
				""".formatted(title, message, buttonUrl, buttonText, buttonUrl);
	}

	/**
	 * OTP email template.
	 */
	private String buildOtpTemplate(String title, String otp) {

		return """
				<!DOCTYPE html>

				<html>

				<body style="
				        font-family:Arial;
				        background:#f5f7fb;
				        padding:40px;">

				    <div style="
				            max-width:600px;
				            margin:auto;
				            background:white;
				            padding:40px;
				            border-radius:12px;
				            text-align:center;
				            box-shadow:0 3px 10px rgba(0,0,0,.1);">

				        <h2>SandBox</h2>

				        <h3>%s</h3>

				        <div style="
				                font-size:32px;
				                letter-spacing:10px;
				                background:#eef3ff;
				                padding:20px;
				                border-radius:10px;
				                margin:30px 0;">

				            %s

				        </div>

				        <p>

				            This OTP is valid for
				            <b>5 minutes</b>.

				        </p>

				        <p>

				            Do not share this OTP
				            with anyone.

				        </p>

				    </div>

				</body>

				</html>
				""".formatted(title, otp);
	}

	/**
	 * Generic email template.
	 */
	private String buildSimpleTemplate(String title, String message) {

		return """
				<!DOCTYPE html>

				<html>

				<body style="
				        font-family:Arial;
				        background:#f5f7fb;
				        padding:40px;">

				    <div style="
				            max-width:600px;
				            margin:auto;
				            background:white;
				            padding:40px;
				            border-radius:12px;
				            text-align:center;">

				        <h2>%s</h2>

				        <p>%s</p>

				    </div>

				</body>

				</html>
				""".formatted(title, message);
	}

	public String buildTestEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * PASSWORD RESET OTP EMAIL
	 *
	 * Generates the HTML email sent during Forgot Password.
	 */
	public String buildOtpTemplate(

			String name,

			String otp,

			int expiryMinutes

	) {

		return """
				<!DOCTYPE html>
				<html>

				<head>

				<meta charset="UTF-8">

				<title>Password Reset</title>

				</head>

				<body style="font-family:Arial;background:#f4f6f8;padding:40px;">

				<div style="

				max-width:600px;

				margin:auto;

				background:white;

				border-radius:12px;

				padding:40px;

				box-shadow:0 3px 12px rgba(0,0,0,.08);

				">

				<h2 style="color:#2563eb;">

				Password Reset Request

				</h2>

				<p>

				Hello <b>%s</b>,

				</p>

				<p>

				We received a request to reset your password.

				</p>

				<p>

				Use the OTP below to continue.

				</p>

				<div style="

				font-size:34px;

				font-weight:bold;

				letter-spacing:10px;

				padding:18px;

				margin:30px 0;

				background:#eef4ff;

				text-align:center;

				border-radius:10px;

				color:#2563eb;

				">

				%s

				</div>

				<p>

				This OTP will expire in

				<b>%d minutes</b>.

				</p>

				<p>

				If you didn't request this,

				please ignore this email.

				</p>

				<hr>

				<p style="color:gray;font-size:13px;">

				Sandbox Examination Platform

				</p>

				</div>

				</body>

				</html>
				""".formatted(

				name,

				otp,

				expiryMinutes

		);

	}

}