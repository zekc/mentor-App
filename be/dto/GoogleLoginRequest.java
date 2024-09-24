package com.obss.mentorapp.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GoogleLoginRequest {
    private String token;

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

