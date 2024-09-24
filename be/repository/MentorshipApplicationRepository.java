package com.obss.mentorapp.repository;

import com.obss.mentorapp.entity.MentorshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipApplicationRepository extends JpaRepository<MentorshipApplication, Long> {

    // Belirli bir durumdaki başvuruları bulmak için
    List<MentorshipApplication> findByStatus(String status);
}
