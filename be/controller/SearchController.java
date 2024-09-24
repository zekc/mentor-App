package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.mapper.CourseMapper;
import com.obss.mentorapp.service.CourseSearchService;
import com.obss.mentorapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final CourseSearchService courseSearchService;
    private final UserService userService;
    private final CourseMapper courseMapper;

    public SearchController(CourseSearchService courseSearchService, UserService userService, CourseMapper courseMapper) {
        this.courseSearchService = courseSearchService;
        this.userService = userService;
        this.courseMapper = courseMapper;
    }

    @GetMapping("/courses")
    public List<CourseDTO> searchCourses(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String topic) {
        List<Course> courses;

        // Logger tanımlaması
        Logger logger = LoggerFactory.getLogger(this.getClass());

        if (!StringUtils.hasText(keyword) && !StringUtils.hasText(topic)) {
            courses = courseSearchService.findAllCourses();
        } else if (StringUtils.hasText(keyword) && StringUtils.hasText(topic)) {
            courses = courseSearchService.searchCoursesByKeywordAndTopic(keyword, topic);
        } else if (StringUtils.hasText(topic)) {
            courses = courseSearchService.searchCoursesByTopic(topic);
        } else {
            courses = courseSearchService.searchCourses(keyword);
        }

        for (Course course : courses) {
            if (course.getMentor() != null) {
                String mentorName = course.getMentor().getUsername();
                logger.info("Course ID: {} - Mentor Name: {}", course.getId(), mentorName);
            } else if (course.getMentorId() != null) {
                Optional<User> mentor = userService.findById(course.getMentorId());
                if (mentor.isPresent()) {
                    String mentorName = mentor.get().getUsername();
                    logger.info("Course ID: {} - Mentor Name: {}", course.getId(), mentorName);
                } else {
                    logger.warn("Course ID: {} - Mentor not found for ID {}", course.getId(), course.getMentorId());
                }
            } else {
                logger.warn("Course ID: {} - Mentor information is not available", course.getId());
            }

            if (course.getMentee() != null) {
                String menteeName = course.getMentee().getUsername();
                logger.info("Course ID: {} - Mentee Name: {}", course.getId(), menteeName);
            } else if (course.getMenteeId() != null) {
                Optional<User> mentee = userService.findById(course.getMenteeId());
                if (mentee.isPresent()) {
                    String menteeName = mentee.get().getUsername();
                    logger.info("Course ID: {} - Mentee Name: {}", course.getId(), menteeName);
                } else {
                    logger.warn("Course ID: {} - Mentee not found for ID {}", course.getId(), course.getMenteeId());
                }
            } else {
                logger.warn("Course ID: {} - Mentee information is not available", course.getId());
            }
        }

        // Courses listesini DTO'ya dönüştürmek için statik olmayan mapper kullanıyoruz
        return courses.stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

}
