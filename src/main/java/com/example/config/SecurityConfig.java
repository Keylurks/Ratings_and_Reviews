package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // ALLOW PUBLIC ACCESS TO: Home, Login, Rating Form, CSS, Images, and QR Generation
                .requestMatchers("/", "/login", "/rate/**", "/css/**", "/js/**", "/images/**", "/admin/qr/**").permitAll()
                
                // RESTRICT ADMIN DASHBOARD
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // REQUIRE LOGIN FOR EVERYTHING ELSE (Like /dashboard)
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login") // This matches the @GetMapping("/login") in Controller
                .permitAll()
                .defaultSuccessUrl("/", true) // Forces redirection to the home() method to check roles
            )
            .logout((logout) -> logout
            	    .logoutUrl("/logout") // Triggered by POST /logout
            	    .logoutSuccessUrl("/login?logout") // Go here after success
            	    .permitAll()
            	);

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // User 1 - Regular Commuter
        UserDetails user = User.builder()
                .username("user1")
                .password(passwordEncoder().encode("user1"))
                .roles("USER")
                .build();

        // Admin 1 - System Administrator
        UserDetails admin = User.builder()
                .username("admin1")
                .password(passwordEncoder().encode("admin1"))
                .roles("ADMIN")
                .build();
                
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encrypts passwords securely
    }
}