package com.obss.mentorapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CourseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Başvuru yapan kullanıcı (mentee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Başvurulan kurs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // Başvuru açıklaması
    @Column(length = 1000)
    private String description;

    // İletişim bilgileri
    private String contactInfo;

    // Başvurunun durumu (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    // Başvuru tarihi
    private LocalDateTime applicationDate;

    // Kursun sahibi olan mentor'a erişim için getter
    public User getMentor() {
        return course.getMentor();
    }

    // Constructor, getters ve setters Lombok tarafından sağlanacak.
}
