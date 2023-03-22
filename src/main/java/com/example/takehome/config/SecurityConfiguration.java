package com.example.takehome.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     Configures the security filter chain.
     @param http HTTP security object
     @return security filter chain
     @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().ignoringRequestMatchers("/api/v1/auth/**")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/actuator/**").authenticated() // require authentication for accessing actuator endpoints
            .requestMatchers("/api/v1/private/**").authenticated() // require authentication for accessing actuator endpoints
            .requestMatchers("/api/v1/public/**").permitAll() // allow for public endpoints
            .requestMatchers("/api/v1/auth/**").permitAll()   // allow for auth endpoints, they will have user/password authentication
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers()
            .xssProtection(); // add protection against XSS attacks
        return http.build();
    }
}
