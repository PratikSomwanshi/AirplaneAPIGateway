package com.wanda.config;

import com.wanda.filter.JWTFilter;
import com.wanda.service.CustomUserDetailsService;
import com.wanda.utils.exception.CustomAccessDeniedHandler;
import com.wanda.utils.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private JWTFilter jwtFilter;
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    // Constructor Injection (Spring automatically wires it)
    public SpringSecurityConfig(CustomUserDetailsService userDetailsService,
                                JWTFilter jwtFilter,
                                CustomAccessDeniedHandler customAccessDeniedHandler,
                                CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) {
        this.customUserDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity auth) throws Exception {
        auth
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.GET, "/airplane").permitAll() // Allow GET /airplane
                        .requestMatchers("/register", "/login").permitAll()       // Allow /register and /login
                        .anyRequest().authenticated()                            // Authenticate all other requests
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception -> {
                            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
                            exception.accessDeniedHandler(customAccessDeniedHandler);
                        }
                );


        return auth.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(customUserDetailsService); // Set the CustomUserDetailsService
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
