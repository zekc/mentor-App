package com.obss.mentorapp.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.obss.mentorapp.dto.GoogleLoginRequest;
import com.obss.mentorapp.dto.LoginRequest;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.security.GoogleTokenVerifier;
import com.obss.mentorapp.security.JwtTokenProvider;
import com.obss.mentorapp.security.UserPrincipal;
import com.obss.mentorapp.service.CustomUserDetailsService;
import com.obss.mentorapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Log giriş bilgilerini
            System.out.println("Login request received for user: " + loginRequest.getUsername());

            // Authentication işlemi (LDAP üzerinden doğrulama yapılır)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Kimlik doğrulama başarılı ise logla
              System.out.println("LDAP authentication successful for user: " + loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Principal'dan username'i alın
            var userPrincipal = ((UserDetails) authentication.getPrincipal());

            // Kullanıcının verilerini DB'den çekin
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                // Eğer kullanıcı veritabanında bulunmazsa hata döndür
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOptional.get();

            // Kullanıcının rollerini alın
            String role = user.getRoles().iterator().next().getName().name();

            // JWT'yi email ile oluşturun
            String jwtToken = tokenProvider.createToken(user.getEmail());

            // JWT oluşturulduktan sonra logla
            System.out.println("JWT generated for user: " + userPrincipal.getUsername());
            System.out.println(jwtToken);

            // Yanıtı Map olarak oluşturun
            Map<String, String> response = new HashMap<>();
            response.put("jwtToken", jwtToken);
            response.put("role", role);
            response.put("name", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Hata durumunu yakala ve logla
            System.err.println("LDAP login failed for user: " + loginRequest.getUsername());
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        String token = googleLoginRequest.getToken();
        GoogleIdToken idToken = googleTokenVerifier.verifyToken(token);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            // Kullanıcıyı kaydederken tüm bilgileri set edin
            User user = userService.saveGoogleUser(
                    User.builder()
                            .email(email)  // email set ediliyor
                            .username(name)    // name set ediliyor
                            .build()
            );

            String jwtToken = tokenProvider.createToken(user.getEmail());

            System.out.println("jwtToken: " + jwtToken);

            // Kullanıcının rolünü alın
            String role = user.getRoles().iterator().next().getName().name();

            // Loglama
            System.out.println("Google login successful: " + email);
            System.out.println("Generated JWT Token: " + jwtToken);

            Map<String, Object> response = new HashMap<>();
            response.put("jwtToken", jwtToken);
            response.put("email", email);
            response.put("name", name);
            response.put("pictureUrl", pictureUrl);
            response.put("role", role); // Rolü yanıt ile gönderiyoruz

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String token) {
        try {
            // JWT'den username veya email'i çıkar
            String usernameOrEmail = tokenProvider.getUsernameFromJwt(token.substring(7));

            UserDetails userDetails;

            // İlk önce username ile kontrol et, eğer bulunamazsa email ile kontrol et
            try {
                userDetails = customUserDetailsService.loadUserByUsername(usernameOrEmail);
            } catch (UsernameNotFoundException e) {
                userDetails = customUserDetailsService.loadUserByEmail(usernameOrEmail);
            }

            // Kullanıcının rolünü alın
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // Yanıt olarak rol bilgisini döndür
            Map<String, String> response = new HashMap<>();
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Hata durumunda uygun bir yanıt döndürülür
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
        }
    }
}
