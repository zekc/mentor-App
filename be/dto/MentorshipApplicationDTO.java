package com.obss.mentorapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MentorshipApplicationDTO {
    private Long id;  // Bu satırı ekleyin
    private String applicantEmail; // Kullanıcı email'i
    private String topicName; // ID yerine isim gönderiliyor
    private Date applicationDate;
    private String status;
    private String description;


}

