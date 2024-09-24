package com.obss.mentorapp.repository;

import com.obss.mentorapp.entity.ApplicationStatus;
import com.obss.mentorapp.entity.CourseApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {
    List<CourseApplication> findByCourseMentor_Id(Long mentorId);
    List<CourseApplication> findByUserId(Long userId);

    List<CourseApplication> findByCourseId(Long courseId);

    List<CourseApplication> findByCourseMentor_IdAndStatus(Long mentorId, ApplicationStatus status);
    List<CourseApplication> findByCourseMentorEmailOrUserEmailAndStatus(String mentorEmail, String userEmail, ApplicationStatus status);

}
