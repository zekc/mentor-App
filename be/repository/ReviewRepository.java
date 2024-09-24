package com.obss.mentorapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.obss.mentorapp.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
