package com.obss.mentorapp.service;

import com.obss.mentorapp.entity.ApplicationStatus;
import com.obss.mentorapp.entity.CourseApplication;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.repository.CourseApplicationRepository;
import com.obss.mentorapp.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CourseApplicationService {

    @Autowired
    private CourseApplicationRepository courseApplicationRepository;

    @Autowired
    private CourseRepository courseRepository;

    public CourseApplication save(CourseApplication application) {
        return courseApplicationRepository.save(application);
    }

    public Optional<CourseApplication> findById(Long id) {
        return courseApplicationRepository.findById(id);
    }

    public List<CourseApplication> findApplicationsByCourseId(Long courseId) {
        return courseApplicationRepository.findByCourseId(courseId);
    }

    public CourseApplication applyForCourse(User user, Course course, String description, String contactInfo) {
        CourseApplication application = new CourseApplication();
        application.setUser(user);
        application.setCourse(course);
        application.setDescription(description);
        application.setContactInfo(contactInfo);
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationDate(LocalDateTime.now());

        return courseApplicationRepository.save(application);
    }

    public List<CourseApplication> getApplicationsForMentor(Long mentorId) {
        return courseApplicationRepository.findByCourseMentor_Id(mentorId);
    }

    @Transactional
    public void approveApplication(Long applicationId) {
        CourseApplication application = courseApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with ID: " + applicationId));

        // Mentee'nin var olup olmadığını kontrol edin
        if (application.getUser() == null) {
            throw new IllegalStateException("Mentee bilgisi bulunamadı");
        }

        // Kursun var olup olmadığını kontrol edin
        Course course = application.getCourse();
        if (course == null) {
            throw new IllegalStateException("Kursa erişilemedi");
        }

        // Başvuruyu onaylayalım
        application.setStatus(ApplicationStatus.APPROVED);
        courseApplicationRepository.save(application);

        // Kursu aktif hale getir ve mentee'yi ata
        course.setMentee(application.getUser());  // Mentee'yi kursa ekliyoruz
        course.setActive(true);  // Kurs artık aktif
        courseRepository.save(course);
    }



    public void rejectApplication(Long applicationId) {
        CourseApplication application = courseApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        // Başvuru durumu güncelleniyor
        application.setStatus(ApplicationStatus.REJECTED);
        courseApplicationRepository.save(application);

        // Kursun durumunu pasife çek
        Course course = application.getCourse();
        if (course != null) {
            course.setActive(false);  // Kursu pasif hale getir
            courseRepository.save(course);  // Kurs bilgisini güncelle
        }
    }



    public List<CourseApplication> getPendingApplicationsByMentor(Long mentorId) {
        return courseApplicationRepository.findByCourseMentor_IdAndStatus(mentorId, ApplicationStatus.PENDING);
    }

    public List<Course> getActiveCoursesForUser(Long userId) {
        return courseRepository.findByMentee_IdOrMentor_Id(userId, userId);
    }

}
