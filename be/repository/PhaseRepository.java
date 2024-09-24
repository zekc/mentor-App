package com.obss.mentorapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.obss.mentorapp.entity.Phase;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByCourse_Id(Long courseId);
}

