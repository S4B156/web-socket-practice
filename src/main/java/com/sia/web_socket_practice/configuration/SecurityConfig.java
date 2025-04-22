package com.sia.web_socket_practice.configuration;

import com.sia.web_socket_practice.entity.User;
import com.sia.web_socket_practice.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/ws/**").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public UserDetailsService loadUserByUsername(UserRepository userRepository){
        return username -> {
            User user = userRepository.findByUsername(username);
            if(user != null) return user;
            throw new UsernameNotFoundException("User ‘" + username + "’ not found");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
