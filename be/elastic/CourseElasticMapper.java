package com.obss.mentorapp.elastic;

import com.obss.mentorapp.entity.Course;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CourseElasticMapper {

    public static CourseElasticDTO toElasticDTO(Course course) {
        CourseElasticDTO dto = new CourseElasticDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setMentorName(course.getMentor() != null ? course.getMentor().getUsername() : null); // Mentor adı
        dto.setMentorId(course.getMentor() != null ? course.getMentor().getId() : null); // Mentor ID
        dto.setMenteeName(course.getMentee() != null ? course.getMentee().getUsername() : null); // Mentee adı
        dto.setCompleted(course.isCompleted()); // Kursun tamamlanma durumu
        return dto;
    }

    public static List<CourseElasticDTO> toElasticDTOList(List<Course> courses) {
        return courses.stream()
                .map(CourseElasticMapper::toElasticDTO)
                .collect(Collectors.toList());
    }
}
