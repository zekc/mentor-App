package com.obss.mentorapp.repository;

import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.Topic;
import com.obss.mentorapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByMentor(User mentor);
    List<Course> findByMentee(User mentee);
    boolean existsByName(String name);
    boolean existsByMentor(User mentor);
    List<Course> findByMentorUsername(String mentorUsername);
    List<Course> findByMenteeUsername(String menteeUsername);

    boolean existsByNameAndMentor(String name, User mentor);

    List<Course> findByMentorEmail(String email);

    List<Course> findByMenteeEmail(String email);

    List<Course> findByMentorEmailOrMenteeEmailAndIsCompletedFalse(String email, String email1);

    List<Course> findByMentee_IdOrMentor_Id(Long menteeId, Long mentorId);

}
