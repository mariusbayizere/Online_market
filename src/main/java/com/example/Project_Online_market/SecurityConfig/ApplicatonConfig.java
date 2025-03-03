package com.example.Project_Online_market.SecurityConfig;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

        @Configuration
        @EnableMethodSecurity
        public class ApplicatonConfig {

            @Bean
            SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                    .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorize -> authorize
                    // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v2/api-docs", "/swagger-ui.html").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/orders/**").hasAnyRole("SHOPPER", "BUYER","ADMIN") // Restrict order placement
                        .requestMatchers("/api/orders/history").authenticated() // Only authenticated users can view order history
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Restrict admin endpoints
                        .requestMatchers("/api/categories/**").hasRole("ADMIN") // Restrict admin endpoints
                        .requestMatchers("/api/products/**").hasRole("ADMIN") // Restrict admin endpoints
                        .requestMatchers("/api/reviews/**").hasRole("ADMIN") // Restrict admin endpoints
                        .anyRequest().permitAll())
                        // .anyRequest().authenticated())
                    .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()));

                return http.build();
            }

            private CorsConfigurationSource corsConfigurationSource() {
                return request -> {
                    CorsConfiguration ccfg = new CorsConfiguration();
                    ccfg.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
                    ccfg.setAllowedMethods(Collections.singletonList("*"));
                    ccfg.setAllowCredentials(true);
                    ccfg.setAllowedHeaders(Collections.singletonList("*"));
                    ccfg.setExposedHeaders(Arrays.asList("Authorization"));
                    ccfg.setMaxAge(3600L);
                    return ccfg;
                };
            }
            @Bean
            PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
            }
        }

