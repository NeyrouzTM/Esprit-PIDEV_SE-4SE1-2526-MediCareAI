package tn.esprit.tn.medicare_ai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration de JavaMailSender pour l'envoi d'emails
 * Cette configuration est appliquée si les propriétés spring.mail sont configurées
 */
@Configuration
@ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}

