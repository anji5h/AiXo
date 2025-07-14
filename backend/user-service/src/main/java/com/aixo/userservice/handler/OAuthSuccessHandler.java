package com.aixo.userservice.handler;

import com.aixo.userservice.provider.JwtTokenProvider;
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

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException{
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String jwt = jwtTokenProvider.generateToken(authToken.getPrincipal());

        String redirectUrl = String.format(redirectUri, "?token=" + jwt);
        response.sendRedirect(redirectUrl);
    }
}
