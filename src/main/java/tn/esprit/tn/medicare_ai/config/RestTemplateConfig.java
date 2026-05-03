package tn.esprit.tn.medicare_ai.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST client calls
 * Used for external API integrations like RapidAPI
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Create a RestTemplate bean for making HTTP requests to external APIs
     * @param builder the RestTemplateBuilder
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(java.time.Duration.ofSeconds(5))
                .setReadTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }
}

