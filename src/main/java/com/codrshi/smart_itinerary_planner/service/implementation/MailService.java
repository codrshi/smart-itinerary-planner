package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.service.IMailService;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService implements IMailService {

    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Retryable(retryFor = {MessagingException.class, MailException.class}, maxAttempts = 3,
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendMail(String subject, String body)
            throws MessagingException {

        log.debug("Prepared mail subject: {}", subject);
        log.debug("Prepared mail body: {}", body);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromMail);
            helper.setTo(RequestContext.getCurrentContext().getEmail());
            helper.setSubject(subject);
            helper.setText(body, false);

            javaMailSender.send(message);
        }
            catch (MessagingException | MailException ex) {
            log.error("Exception while sending email: {}, retry will be triggered", ex.getMessage());
            throw ex;
        }
            catch (Exception ex) {
            log.error("Exception while sending email: {}", ex.getMessage());
            throw new RuntimeException("Failed to send email", ex);
        }

        log.info("Email sent successfully.");
    }

}
