 package com.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.ecom.service.UserService;
import com.ecom.util.ActiveUserStore;

@Configuration
public class SecurityConfig {

    private final ActiveUserStore activeUserStore;

    public SecurityConfig(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http,
            UserService userService) throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ DISABLE CSRF FOR API CALLS
            .csrf(csrf -> csrf.disable())
            
            .headers(headers -> headers
                    .frameOptions(frame -> frame.disable()) // Required for WebRTC video frames
                )
         

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/signup",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/favicon.ico",
                        "/error",
                        "/ws/**"
                       
                       
                        
                ).permitAll()

                // ✅ API requires login
                .requestMatchers("/video").authenticated()
                .requestMatchers("/api/**").authenticated()

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutRequestMatcher(
                        new AntPathRequestMatcher("/logout", "GET")
                )
                .logoutSuccessUrl("/login?logout")
                .addLogoutHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        activeUserStore.removeUser(authentication.getName());
                    }
                })
                .permitAll()
            )

            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            );

        return http.build();
    }
}
