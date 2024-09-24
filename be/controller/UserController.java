package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.UserDTO;
import com.obss.mentorapp.entity.Role;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.mapper.UserMapper;
import com.obss.mentorapp.security.JwtTokenProvider;
import com.obss.mentorapp.security.UserPrincipal;
import com.obss.mentorapp.service.RoleService;
import com.obss.mentorapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper userMapper;  // UserMapper'ı otomatik olarak inject ediyoruz

    @PostMapping(value = "/add", consumes = {"application/xml", "application/json"})
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);  // Manuel çağrı yerine inject edilen mapper kullanılıyor

        // Save roles if not already present in the database
        Set<Role> roles = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role existingRole = roleService.findByName(role.getName())
                    .orElseGet(() -> roleService.save(new Role(role.getName())));
            roles.add(existingRole);
        }
        user.setRoles(roles);

        UserDTO createdUser = userService.registerUser(userMapper.toDTO(user));  // Kayıt metodunu, createUser metoduna uyacak şekilde güncellemen gerekebilir
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromJwt(token.substring(7)); // "Bearer " önekini kaldırıyoruz
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Role> roles = user.getRoles();

            if (!roles.isEmpty()) {
                // Role enum'ını String'e dönüştürme
                String roleName = roles.iterator().next().getName().name();
                return ResponseEntity.ok(Collections.singletonMap("role", roleName));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found for the user");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            // Log error and return a bad request response if userPrincipal is null
            System.err.println("UserPrincipal is null. No user is authenticated.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not authenticated");
        }

        // Log the username or email retrieved from the UserPrincipal
        System.out.println("Retrieving user: " + userPrincipal.getUsername());

        Optional<User> user = userService.findByUsername(userPrincipal.getUsername());

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            System.err.println("User not found for email: " + userPrincipal.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    // other endpoint methods
}
