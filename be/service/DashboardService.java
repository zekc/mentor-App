package com.obss.mentorapp.service;

import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.dto.DashboardDTO;
import com.obss.mentorapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    public DashboardDTO getDashboardData(String username) {
        // Kullanıcı bilgilerini alın
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Kullanıcı rollerini al
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        // Mentor olduğu kursları al
        List<CourseDTO> mentorCourses = courseService.findCoursesByMentor(user).stream()
                .map(course -> CourseDTO.builder()
                        .mentorName(course.getMentor().getUsername())
                        .menteeName(course.getMentee().getUsername())
                        .description(course.getDescription())
                        .isCompleted(course.isCompleted())
                        .build())
                .collect(Collectors.toList());

        // Mentee olduğu kursları al
        List<CourseDTO> menteeCourses = courseService.findCoursesByMentee(user).stream()
                .map(course -> CourseDTO.builder()
                        .mentorName(course.getMentor().getUsername())
                        .menteeName(course.getMentee().getUsername())
                        .description(course.getDescription())
                        .isCompleted(course.isCompleted())
                        .build())
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .mentorCourses(mentorCourses)
                .menteeCourses(menteeCourses)
                .build();
    }
}

