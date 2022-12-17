package com.ms.email.services;

import com.ms.email.enums.StatusEmail;
import com.ms.email.models.EmailModel;
import com.ms.email.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    JavaMailSender emailSender;

    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setSendDateEmail(LocalDateTime.now());
        try{
            var message = emailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");


            helper.setFrom(emailModel.getEmailFrom());
            helper.setTo(emailModel.getEmailTo());
            helper.setSubject(emailModel.getSubject());
            helper.setPriority(1);
            //inserimos a data de envio
            helper.setSentDate(new Date());
            helper.setText("<html><head>" +
                            "<p>Mensagem:" + emailModel.getText() + "</p>" +
                            "</body></html>",
                        true
                    );

            // helper.addInline("teste", new ClassPathResource("archives/teste.jpg"), "image/jpg");
            emailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);
        }catch (MailException e){
            emailModel.setStatusEmail(StatusEmail.ERROR);
            System.out.println("Email não pode ser eviado!\n" + e.getMessage());
        }catch (MessagingException e){
            System.out.println("Email não pode ser eviado.\n" + e.getMessage());
        } finally {
            return emailRepository.save(emailModel);
        }
    }
}
