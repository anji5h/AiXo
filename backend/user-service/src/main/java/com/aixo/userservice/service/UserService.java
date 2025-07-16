package com.aixo.userservice.service;

import com.aixo.userservice.config.AppConfig;
import com.aixo.userservice.enums.OAuthProvider;
import com.aixo.userservice.exception.NotFoundException;
import com.aixo.userservice.model.User;
import com.aixo.userservice.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User upsertUser(OAuth2User authUser) {
        String email = getAttribute(authUser, AppConfig.OAuth2.ATTRIBUTE_EMAIL);
        String name = getAttribute(authUser, AppConfig.OAuth2.ATTRIBUTE_NAME);
        String oAuthId = getAttribute(authUser, AppConfig.OAuth2.ATTRIBUTE_SUB);
        String imageURL = getAttribute(authUser, AppConfig.OAuth2.ATTRIBUTE_PICTURE);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        var user = optionalUser.orElse(new User());
        user.setEmail(email);
        user.setName(name);
        user.setOAuthId(oAuthId);
        user.setImageURL(imageURL);
        user.setOAuthProvider(OAuthProvider.GOOGLE);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }


    private String getAttribute(OAuth2User user, String attribute) {
        return Optional.ofNullable(user.getAttribute(attribute))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Missing OAuth2 attribute: " + attribute));
    }
}
