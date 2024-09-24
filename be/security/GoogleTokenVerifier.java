package com.obss.mentorapp.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleTokenVerifier {

    private static final String CLIENT_ID = "55195708695-qlb0eenv7mv2hrt4s0uuq5aepqgksqpg.apps.googleusercontent.com";


    private static final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .setIssuer("https://accounts.google.com")
            .build();

    private static ArrayList<String> tokenList = new ArrayList<String>();

    public GoogleIdToken verifyToken(String tokenString) {
        if (tokenString == null || tokenString.isEmpty()) {
            System.err.println("Boş veya null token ile doğrulama yapılamaz.");
            return null;
        }
        tokenList.add(tokenString);
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(tokenString);
            if (idToken == null) {
                System.err.println("Doğrulama başarısız oldu, geçersiz token.");
            } else {
                System.out.println("Token doğrulandı.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Token geçersiz: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Token doğrulama sırasında hata oluştu: " + e.getMessage());
        }
        System.out.println(tokenList);
        return idToken;
    }
}
