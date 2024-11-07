//package com.prodify.cqrs.gatewayservice.config;
//
//import com.prodify.cqrs.gatewayservice.filter.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
//                .authorizeHttpRequests(authRequests -> authRequests
//                        .requestMatchers(new AntPathRequestMatcher("/auth/**"),
//                                new AntPathRequestMatcher("/v3/api-docs/**"),
//                                new AntPathRequestMatcher("/swagger-ui.html"),
//                                new AntPathRequestMatcher("/swagger-ui/**"))
//                        .permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
//
//
//        return http.build();
//    }
//
//    @Configuration
//    public static class SwaggerConfig {
//        // Additional Swagger configuration can be added here if necessary
//    }
//}
