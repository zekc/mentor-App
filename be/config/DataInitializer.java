package com.obss.mentorapp.config;

import com.obss.mentorapp.entity.Role;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.repository.RoleRepository;
import com.obss.mentorapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        addRoleIfNotFound(Role.RoleName.ROLE_USER);
        addRoleIfNotFound(Role.RoleName.ROLE_ADMIN);
        addUserIfNotFound("admin", "password123456", Role.RoleName.ROLE_ADMIN);
    }

    private void addRoleIfNotFound(Role.RoleName roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            logger.info("Role {} created", roleName);
        } else {
            logger.info("Role {} already exists", roleName);
        }
    }

    private void addUserIfNotFound(String username, String password, Role.RoleName roleName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);

            User user = new User();
            user.setUsername(username);

            // Şifreyi yalnızca bcrypt formatında değilse şifreleyin
            if (!password.startsWith("$2a$")) { // bcrypt şifreler "$2a$" ile başlar
                password = passwordEncoder.encode(password);
            }
            user.setPassword(password);

            user.setRoles(roles);
            userRepository.save(user);
            logger.info("User {} created with role {}", username, roleName);
        } else {
            logger.info("User {} already exists", username);
        }
    }

}
