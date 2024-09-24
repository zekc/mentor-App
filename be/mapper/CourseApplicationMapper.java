package com.obss.mentorapp.mapper;

import com.obss.mentorapp.dto.CourseApplicationDTO;
import com.obss.mentorapp.entity.ApplicationStatus;
import com.obss.mentorapp.entity.CourseApplication;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.service.CourseService;
import com.obss.mentorapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CourseApplicationMapper {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    // Tarih formatı için formatter
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // DTO'dan Entity'ye dönüştürme
    public CourseApplication toEntity(CourseApplicationDTO dto) {
        CourseApplication application = new CourseApplication();

        // Başvuru yapan kullanıcıyı bul (mentee)
        User mentee = userService.findById(dto.getMenteeId())
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found with ID: " + dto.getMenteeId()));

        // Course'u bul ve mentor bilgisini elde et
        Course course = courseService.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + dto.getCourseId()));

        // Mentee ve Course bilgilerini application entity'ye set et
        application.setUser(mentee); // Başvuru yapan kullanıcı (mentee)
        application.setCourse(course); // Başvurulan kurs

        application.setDescription(dto.getDescription()); // Başvuru açıklaması
        application.setContactInfo(dto.getContactInfo()); // İletişim bilgileri
        application.setStatus(ApplicationStatus.PENDING); // Başvuru durumu başlangıçta 'PENDING' olarak ayarlanır
        application.setApplicationDate(LocalDateTime.now()); // Başvuru tarihi

        return application;
    }

    // Entity'den DTO'ya dönüştürme
    public CourseApplicationDTO toDTO(CourseApplication entity) {
        return CourseApplicationDTO.builder()
                .id(entity.getId()) // Başvuru ID'si
                .menteeId(entity.getUser() != null ? entity.getUser().getId() : null) // Mentee ID'si, yoksa null
                .menteeName(entity.getUser() != null ? entity.getUser().getUsername() : "Mentee bilgisi eksik") // Mentee adı
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null) // Kurs ID'si, yoksa null
                .courseName(entity.getCourse() != null ? entity.getCourse().getName() : "Kurs bilgisi eksik") // Kurs adı
                .description(entity.getDescription()) // Başvuru açıklaması
                .contactInfo(entity.getContactInfo()) // İletişim bilgileri
                .status(entity.getStatus().name()) // Başvurunun durumu
                .applicationDate(entity.getApplicationDate().format(formatter)) // Başvuru tarihi
                .build();
    }
}
