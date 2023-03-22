package com.example.takehome.config;

import com.example.takehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 Configuration class for implementing  Spring Security authentication.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    /**
     Creates a user details service bean.
     @return user details service
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userName -> userRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for userName = " + userName));
    }

    /**
     Creates an authentication provider bean.
     @return authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(userDetailsService());

        // Set the password encoder
        daoAuthProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthProvider;
    }

    /**
     Creates a password encoder bean.
     @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     Creates an authentication manager bean.
     @param config authentication configuration
     @return authentication manager
     @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
