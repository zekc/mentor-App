package com.obss.mentorapp.service;

import com.obss.mentorapp.config.DataInitializer;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordUpdater {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUpdater.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void updatePasswordsToBCrypt() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String password = user.getPassword();
            // Şifre null değilse ve bcrypt formatında değilse
            if (password != null && !password.startsWith("$2a$")) {
                String bcryptPassword = passwordEncoder.encode(password);
                user.setPassword(bcryptPassword);
                userRepository.save(user);
                logger.info("User {}'s password has been updated to BCrypt format", user.getUsername());
            } else if (password == null) {
                logger.warn("User {} has null password. Password update skipped.", user.getUsername());
            }
        }
    }
}
