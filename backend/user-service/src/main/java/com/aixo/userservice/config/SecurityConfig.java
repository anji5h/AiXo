package com.aixo.userservice.config;

import com.aixo.userservice.handler.OAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;

    public SecurityConfig(OAuthSuccessHandler oAuthSuccessHandler) {
        this.oAuthSuccessHandler = oAuthSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth -> auth.requestMatchers("/actuator/**", "/oauth2/**", "/login/**", "/api/user/**")
                                .permitAll()
                                .anyRequest().denyAll())
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuthSuccessHandler))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
