package com.example.minitwitter.global.config;

import com.example.minitwitter.auth.jwt.JwtAuthenticationFilter;
import com.example.minitwitter.auth.jwt.JwtTokenProvider;
import com.example.minitwitter.global.security.SecurityErrorResponseWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    SecurityConfig(SecurityErrorResponseWriter securityErrorResponseWriter, JwtTokenProvider jwtTokenProvider) {
        this.securityErrorResponseWriter = securityErrorResponseWriter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // Health
                .requestMatchers(HttpMethod.GET, "/health").permitAll()

                // Auth / Signup
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // Public User
                .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/by-nickname").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/profile").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/followers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/followings").permitAll()

                // Public Post
                .requestMatchers(HttpMethod.GET, "/api/posts/feed").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/timeline").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/*").permitAll()

                // H2
                .requestMatchers("/h2-console/**").permitAll()

                // Later public images
                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                // Others need auth !!
                .anyRequest().authenticated())
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint((request, response, authException) -> {
                                    securityErrorResponseWriter.write(
                                            response,
                                            HttpServletResponse.SC_UNAUTHORIZED,
                                            "AUTHENTICATION_REQUIRED",
                                            "인증이 필요합니다.");
                                })
                                .accessDeniedHandler((request, response, authException) -> {
                                    securityErrorResponseWriter.write(
                                            response,
                                            HttpServletResponse.SC_FORBIDDEN,
                                            "ACCESS_DENIED",
                                            "접근 권한이 없습니다.");
                                }));

        http.httpBasic(httpBasic -> httpBasic.disable());
        http.formLogin(formLogin -> formLogin.disable());

        http.addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class
        );

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
