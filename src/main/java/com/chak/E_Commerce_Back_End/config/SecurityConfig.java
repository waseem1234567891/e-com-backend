package com.chak.E_Commerce_Back_End.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf(csrf -> csrf.disable()) // Updated way to disable CSRF
               .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/**").permitAll()
                      .anyRequest().authenticated()
               );
       return http.build();
   }
}
