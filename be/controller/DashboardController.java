package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.dto.DashboardDTO;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.mapper.CourseMapper;
import com.obss.mentorapp.service.CourseService;
import com.obss.mentorapp.service.DashboardService;
import com.obss.mentorapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboard(Authentication authentication) {
        String username = authentication.getName();
        DashboardDTO dashboardData = dashboardService.getDashboardData(username);


//        User mentor = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
//        List<Course> mentorCourses = courseService.findCoursesByMentor(mentor);
//
//        User mentee = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
//        List<Course> menteeCourses = courseService.findCoursesByMentee(mentee);
//
//        // Course -> CourseDTO dönüşümü
//        List<CourseDTO> mentorCourseDTOs = CourseMapper.toDTOList(mentorCourses);
//        List<CourseDTO> menteeCourseDTOs = CourseMapper.toDTOList(menteeCourses);

        dashboardData.setMentorCourses(dashboardData.getMentorCourses());
        dashboardData.setMenteeCourses(dashboardData.getMenteeCourses());

        return ResponseEntity.ok(dashboardData);
    }
}


