package com.hangoutz.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails user =
                User.builder().username("user").password("{noop}password").roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                configurer ->
                        configurer
                                .requestMatchers(HttpMethod.GET, "**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/events").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("USER")
                                .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("USER")
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("USER")
        );

        httpSecurity.httpBasic(Customizer.withDefaults());
        httpSecurity.csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }
}
