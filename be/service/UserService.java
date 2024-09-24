package com.obss.mentorapp.service;

import com.obss.mentorapp.dto.UserDTO;
import com.obss.mentorapp.entity.Role;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.mapper.UserMapper;
import com.obss.mentorapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User saveGoogleUser(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        var userOpt = userRepository.findByEmail(user.getEmail());
        return userOpt.orElseGet(() -> {
            var roles = new HashSet<Role>();
            roleService.findByName(Role.RoleName.ROLE_USER).ifPresent(roles::add);
            user.setRoles(roles);

            User savedUser = userRepository.save(user);

            log.info("New Google user saved: {}", savedUser.getEmail());

            return savedUser;
        });
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Yeni kullanıcı kaydetme metodu
    public UserDTO registerUser(UserDTO userDTO) {
        // Şifreyi encode et
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // DTO'yu Entity'ye dönüştür
        User user = userMapper.toEntity(userDTO);

        // Varsayılan rolü ata (örneğin ROLE_USER)
        var roles = new HashSet<Role>();
        roleService.findByName(Role.RoleName.ROLE_USER).ifPresent(roles::add);
        user.setRoles(roles);

        // Kullanıcıyı kaydet
        User savedUser = userRepository.save(user);

        log.info("New user registered: {}", savedUser.getEmail());

        // Entity'yi DTO'ya dönüştür ve döndür
        return userMapper.toDTO(savedUser);
    }
}
