package com.obss.mentorapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "phase")
@Data
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phase_name")  // Veritabanındaki 'name' alanı ile eşleşiyor
    private String phaseName;

    private String status;

    private LocalDate endDate;  // Daha uygun bir tarih tipi
    private Integer rating;
    private String evaluation;
    private String description;  // Tabloda mevcut olan description alanı eklendi

    private boolean completed;  // Tablodaki completed alanı eklendi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore  // Bu alan serileştirme sırasında dikkate alınmayacak
    private Course course;

    // Getters and Setters (Lombok'un @Data anotasyonu ile otomatik oluşturulur)
}


