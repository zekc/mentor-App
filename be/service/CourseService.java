package com.obss.mentorapp.service;

import com.obss.mentorapp.dto.CompletePhaseDTO;
import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.entity.*;
import com.obss.mentorapp.repository.CourseApplicationRepository;
import com.obss.mentorapp.repository.CourseRepository;
import com.obss.mentorapp.repository.PhaseRepository;
import com.obss.mentorapp.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private CourseApplicationRepository courseApplicationRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> findCoursesByMentor(User mentor) {
        return courseRepository.findByMentor(mentor);
    }

    public List<Course> findCoursesByMentee(User mentee) {
        return courseRepository.findByMentee(mentee);
    }

    public Optional<Course> findById(Long courseId) { return courseRepository.findById(courseId); }

    public String getUserEmailFromToken(String token) {
        try {
            // JwtTokenProvider'dan getUsernameFromJwt metodu ile email'i alıyoruz
            return jwtTokenProvider.getUsernameFromJwt(token);
        } catch (Exception e) {
            // Token geçersizse veya başka bir hata olursa, loglama yapabiliriz
            log.error("Error while extracting email from token: {}", e.getMessage());
            return null;
        }
    }

    public List<Course> findCoursesByMentorEmail(String email) {
        return courseRepository.findByMentorEmail(email);
    }

    public List<Course> findCoursesByMenteeEmail(String email) {
        return courseRepository.findByMenteeEmail(email);
    }

    public List<CourseDTO> findActiveCoursesByEmail(String email) {
        List<CourseApplication> approvedApplications = courseApplicationRepository
                .findByCourseMentorEmailOrUserEmailAndStatus(email, email, ApplicationStatus.APPROVED);

        List<CourseDTO> activeCourses = new ArrayList<>();
        for (CourseApplication application : approvedApplications) {
            var course = application.getCourse().toModel();  // DTO'ya dönüşüm yapılırken mentor/mentee eksik olabilir mi?
            if (course.isActive()) {  // Kurs aktifse listeye ekle
                activeCourses.add(course);
            }
        }
        return activeCourses;
    }

    public List<CompletePhaseDTO> getPhasesByCourseId(Long courseId) {
        return phaseRepository.findByCourse_Id(courseId)
                .stream().map(m->CompletePhaseDTO.builder().completed(m.isCompleted()).build()).toList();  // Kursa bağlı fazları bulur
    }

}