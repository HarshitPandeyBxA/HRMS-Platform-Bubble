package com.example.security.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

//    public void sendPasswordResetEmail(String toEmail, String resetLink) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("HRMS Password Reset");
//        message.setText(
//                "You requested a password reset.\n\n" +
//                        "Click the link below to reset your password:\n" +
//                        resetLink + "\n\n" +
//                        "This link is valid for 15 minutes.\n\n" +
//                        "If you did not request this, please ignore this email."
//        );
//
//        mailSender.send(message);
//    }

//    public void sendCredentials(
//            String personalEmail,
//            String companyEmail,
//            String tempPassword) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);
//        message.setTo(personalEmail);
//        message.setSubject("Your Company Login Credentials");
//        message.setText(
//                "Welcome to the Bounteous!\n\n" +
//                        "Company Email: " + companyEmail + "\n" +
//                        "Temporary Password: " + tempPassword + "\n\n" +
//                        "Please login and change your password immediately."
//        );
//
//        mailSender.send(message);
//    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("HRMS Password Reset Request");

            helper.setText(buildPasswordResetHtml(resetLink), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }


    public void sendCredentials(
            String personalEmail,
            String companyEmail,
            String tempPassword) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(personalEmail);
            helper.setSubject("Welcome to the Company – Login Credentials");

            helper.setText(buildHtmlContent(companyEmail, tempPassword), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildHtmlContent(String companyEmail, String tempPassword) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f6f8;
                        padding: 20px;
                    }
                    .container {
                        max-width: 600px;
                        margin: auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        background-color: #743089;
                        color: #ffffff;
                        padding: 20px;
                        text-align: center;
                        font-size: 20px;
                        font-weight: bold;
                    }
                    .content {
                        padding: 25px;
                        color: #333333;
                        line-height: 1.6;
                    }
                    .credentials {
                        background-color: #f1f5f9;
                        padding: 15px;
                        border-radius: 6px;
                        margin: 20px 0;
                        font-size: 15px;
                    }
                    .credentials p {
                        margin: 8px 0;
                    }
                    .footer {
                        text-align: center;
                        padding: 15px;
                        font-size: 12px;
                        color: #777777;
                        background-color: #fafafa;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        Welcome to Bounteous
                    </div>

                    <div class="content">
                        <p>Hello,</p>

                        <p>
                            We are excited to have you onboard.
                            Your company account has been successfully created.
                        </p>

                        <div class="credentials">
                            <p><strong>Company Email:</strong> %s</p>
                            <p><strong>Temporary Password:</strong> %s</p>
                        </div>

                        <p>
                            Please log in using the above credentials and
                            <strong>change your password immediately</strong>
                            during your first login.
                        </p>

                        <p>
                            If you have any issues accessing your account,
                            please contact the HR or IT support team.
                        </p>

                        <p>Best regards,<br/>
                           <strong>HR Team</strong></p>
                    </div>

                    <div class="footer">
                        This is an automated email. Please do not reply.
                    </div>
                </div>
            </body>
            </html>
        """.formatted(companyEmail, tempPassword);
    }

    private String buildPasswordResetHtml(String resetLink) {

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f6f8;
                    padding: 20px;
                }
                .container {
                    max-width: 600px;
                    margin: auto;
                    background-color: #ffffff;
                    border-radius: 8px;
                    overflow: hidden;
                    box-shadow: 0 0 10px rgba(0,0,0,0.1);
                }
                .header {
                    background-color: #743089;
                    color: #ffffff;
                    padding: 20px;
                    text-align: center;
                    font-size: 20px;
                    font-weight: bold;
                }
                .content {
                    padding: 25px;
                    color: #333333;
                    line-height: 1.6;
                }
                .button {
                    display: inline-block;
                    background-color: #743089;
                    color: #ffffff !important;
                    padding: 12px 20px;
                    text-decoration: none;
                    border-radius: 5px;
                    font-weight: bold;
                    margin: 20px 0;
                }
                .warning {
                    background-color: #fef3c7;
                    padding: 15px;
                    border-left: 4px solid #f59e0b;
                    margin: 20px 0;
                    font-size: 14px;
                }
                .footer {
                    text-align: center;
                    padding: 15px;
                    font-size: 12px;
                    color: #777777;
                    background-color: #fafafa;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    Password Reset Request
                </div>

                <div class="content">
                    <p>Hello,</p>

                    <p>
                        We received a request to reset your HRMS account password.
                        Click the button below to proceed.
                    </p>

                    <p style="text-align:center;">
                        <a href="%s" class="button">
                            Reset Password
                        </a>
                    </p>

                    <div class="warning">
                        ⏱️ This reset link is valid for <strong>15 minutes</strong>.
                        If the link expires, please request a new password reset.
                    </div>

                    <p>
                        If you did not request this password reset,
                        please ignore this email. Your account remains secure.
                    </p>

                    <p>
                        For assistance, contact the HR or IT support team.
                    </p>

                    <p>Best regards,<br/>
                       <strong>HR Team</strong></p>
                </div>

                <div class="footer">
                    This is an automated email. Please do not reply.
                </div>
            </div>
        </body>
        </html>
    """.formatted(resetLink);
    }

}


