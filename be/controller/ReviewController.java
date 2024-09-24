package com.obss.mentorapp.controller;

import com.obss.mentorapp.entity.Review;
import com.obss.mentorapp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.findAll();
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.save(review);
    }
}
