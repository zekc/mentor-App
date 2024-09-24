package com.obss.mentorapp.mapper;

import com.obss.mentorapp.dto.CompletePhaseDTO;
import com.obss.mentorapp.dto.CourseDTO;
import com.obss.mentorapp.elastic.CourseElasticDTO;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.Phase;
import com.obss.mentorapp.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    private final UserService userService;
    private final PhaseMapper phaseMapper;

    // Constructor injection for UserService and PhaseMapper
    public CourseMapper(UserService userService, PhaseMapper phaseMapper) {
        this.userService = userService;
        this.phaseMapper = phaseMapper;
    }

    // Convert from CourseElasticDTO to Course entity
    public Course toEntity(CourseElasticDTO dto) {
        if (dto == null) {
            return null;
        }
        Course course = new Course();
        course.setId(dto.getId());
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCompleted(dto.isCompleted());

        // Set mentor if mentorId exists
        if (dto.getMentorId() != null) {
            course.setMentor(userService.findById(dto.getMentorId()).orElse(null));
        }

        return course;
    }

    // Convert list of CourseElasticDTO to list of Course entities
    public List<Course> toEntityList(List<CourseElasticDTO> dtoList) {
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // Convert Course entity to CourseDTO including phases
    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .mentorName(course.getMentor() != null ? course.getMentor().getUsername() : "Mentor bilgisi bulunamadı")
                .menteeName(course.getMentee() != null ? course.getMentee().getUsername() : "Henüz mentee atanmadı")
                .description(course.getDescription())
                .isCompleted(course.isCompleted())
                .isActive(course.isActive())
                .phase(course.getPhases() != null
                        ? course.getPhases().stream().map(this::mapPhaseToDTO).collect(Collectors.toList())
                        : List.of())  // Check if phases are null, return empty list if true
                .build();
    }

    // Map a single Phase entity to CompletePhaseDTO
    private CompletePhaseDTO mapPhaseToDTO(Phase phase) {
        if (phase == null) {
            return null;
        }

        // Logger ile faz verilerini kontrol edebilirsiniz
        System.out.println("Mapping phase: " + phase.getId() + ", Phase Name: " + phase.getPhaseName() + ", Status: " + phase.getStatus());

        return CompletePhaseDTO.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName() != null ? phase.getPhaseName() : "Faz ismi bulunamadı")  // Null kontrolü
                .status(phase.getStatus() != null ? phase.getStatus() : "Durum bilgisi eksik")  // Null kontrolü
                .completed(phase.isCompleted())  // Tamamlanma durumu
                .endDate(phase.getEndDate())  // Bitiş tarihi
                .rating(phase.getRating())  // Puan
                .evaluation(phase.getEvaluation())  // Değerlendirme
                .courseId(phase.getCourse() != null ? phase.getCourse().getId() : null)  // Kursun ID'si
                .build();
    }


    // Convert Course entity to CourseDTO without phases
    public CourseDTO toDTOWithoutPhases(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .mentorName(course.getMentor() != null ? course.getMentor().getUsername() : "Mentor bilgisi bulunamadı")
                .menteeName(course.getMentee() != null ? course.getMentee().getUsername() : "Henüz mentee atanmadı")
                .description(course.getDescription())
                .isCompleted(course.isCompleted())
                .isActive(course.isActive())
                .build();  // Fazları dahil etmiyoruz
    }
}
