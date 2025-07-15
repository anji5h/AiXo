package com.aixo.userservice.handler;

import com.aixo.userservice.provider.JwtTokenProvider;
import com.aixo.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException{
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        var user = userService.upsertUser(authToken.getPrincipal());
        String jwtToken = jwtTokenProvider.generateToken(user);

        String redirectUrl = String.format("%s?token=%s", redirectUri, jwtToken);
        response.sendRedirect(redirectUrl);
    }
}
