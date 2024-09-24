package com.obss.mentorapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";


    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}

