package com.tutu.finsmanager.email;

import com.tutu.finsmanager.registration.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

@Service
@AllArgsConstructor
public class HostingMail {
    public void send(String to, String email) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.port", "...");
            prop.put("mail.smtp.host", "...");

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("...", "...");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("..."));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Активация аккаунта ...");
            //message.setText(email);
            message.setContent(email, "text/html; charset=utf-8");
            //message.setSentDate(new Date());
            Transport.send(message);
            System.out.println("MailDebuger -> send ");
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }
}
