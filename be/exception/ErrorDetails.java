package com.obss.mentorapp.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
