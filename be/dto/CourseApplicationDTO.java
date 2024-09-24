package com.obss.mentorapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseApplicationDTO {

    private Long id;  // Başvurunun ID'si (Entity'den dönerken kullanılacak)

    private Long courseId;  // Başvurulan kursun ID'si
    private Long menteeId;  // Başvuru yapan mentee'nin ID'si (JWT'den alınacak)
    private String description;  // Başvuru açıklaması
    private String contactInfo;  // İletişim bilgileri

    // Bu alanlar sadece veri dönerken kullanılacak:
    private Long mentorId;  // Kursun sahibi olan mentor'un ID'si
    private String courseName;  // Kursun adı
    private String menteeName;  // Mentee'nin adı
    private String mentorName;  // Mentor'un adı
    private String status;  // Başvurunun durumu
    private String applicationDate;  // Başvuru tarihi (Formatted string)
}
