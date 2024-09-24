package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.CourseApplicationDTO;
import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.CourseApplication;
import com.obss.mentorapp.mapper.CourseApplicationMapper;
import com.obss.mentorapp.mapper.CourseMapper;
import com.obss.mentorapp.service.CourseApplicationService;
import com.obss.mentorapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseApplicationService courseApplicationService;

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.findAll();
    }

    @PostMapping("/add")
    public Course createCourse(@RequestBody Course course) {
        return courseService.save(course);
    }

    // Kullanıcının mentor veya mentee olduğu kursları döndüren endpoint
    @GetMapping("/my-courses")
    public Map<String, List<Course>> getMyCourses(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " kelimesini kaldır
        }
        String userEmail = courseService.getUserEmailFromToken(token);  // Token'dan kullanıcı emailini alın
        List<Course> mentorCourses = courseService.findCoursesByMentorEmail(userEmail);
        List<Course> menteeCourses = courseService.findCoursesByMenteeEmail(userEmail);
        Map<String, List<Course>> response = new HashMap<>();
        response.put("mentorCourses", mentorCourses);
        response.put("menteeCourses", menteeCourses);

        return response;
    }

    @GetMapping("/my-courses/applications")
    public ResponseEntity<Map<String, List<CourseApplicationDTO>>> getCourseApplications(
            @RequestHeader("Authorization") String token) {

        String userEmail = courseService.getUserEmailFromToken(token.substring(7));  // "Bearer " kelimesini kaldırıyoruz
        List<Course> mentorCourses = courseService.findCoursesByMentorEmail(userEmail);

        Map<String, List<CourseApplicationDTO>> response = new HashMap<>();

        for (Course course : mentorCourses) {
            List<CourseApplication> applications = courseApplicationService.findApplicationsByCourseId(course.getId());
            List<CourseApplicationDTO> applicationDTOs = applications.stream()
                    .map(courseApplicationMapper::toDTO)
                    .collect(Collectors.toList());
            response.put(course.getName(), applicationDTOs);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CourseDTO>> getActiveCourses(@RequestHeader("Authorization") String token) {
        String userEmail = courseService.getUserEmailFromToken(token.substring(7));

        List<CourseDTO> activeCourses = courseService.findActiveCoursesByEmail(userEmail);

        // Eğer aktif kurs yoksa boş bir liste döndür
        if (activeCourses == null || activeCourses.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(activeCourses);
    }


    // Yeni metod fazları kursa göre döndürecek
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseDetails(@PathVariable Long courseId) {
        // Kursu bul, Optional kullanarak
        Optional<Course> optionalCourse = courseService.findById(courseId);
        // Eğer kurs bulunamazsa 404 döndür
        if (optionalCourse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Course course = optionalCourse.get();
        // Course'u DTO'ya dönüştür
        CourseDTO courseDTO = courseMapper.toDTO(course);
        // Kursun fazlarını yükleyelim ve ekleyelim
        return ResponseEntity.ok(courseDTO);
    }

}

