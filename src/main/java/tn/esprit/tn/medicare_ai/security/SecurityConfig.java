package tn.esprit.tn.medicare_ai.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.esprit.tn.medicare_ai.security.jwt.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p =
                new DaoAuthenticationProvider(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // Medical Records
                        .requestMatchers("/medical-records/**")
                        .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        // Prescriptions
                        .requestMatchers("/prescriptions/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Lab Results
                        .requestMatchers("/lab-results/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Medical Images
                        .requestMatchers("/medical-images/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Allergies
                        .requestMatchers("/allergies/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Visit Notes
                        .requestMatchers("/visit-notes/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Appointments
                        .requestMatchers("/appointments/**")
                        .hasAnyRole("PATIENT", "DOCTOR")
                        // Availabilities
                        .requestMatchers("/availabilities/**")
                        .hasAnyRole("DOCTOR", "PATIENT")
                        // Role based endpoints
                        .requestMatchers("/patient/**")
                        .hasRole("PATIENT")
                        .requestMatchers("/doctor/**")
                        .hasRole("DOCTOR")
                        .requestMatchers("/pharmacy/**")
                        .hasRole("PHARMACIST")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        // Any other request
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}