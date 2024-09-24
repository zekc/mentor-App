package com.obss.mentorapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long id;  // Course için benzersiz kimlik
    private String name;  // Course adı
    private String courseName;
    private String mentorName;  // Mentorluk yapılıyorsa, mentor ismi
    private String menteeName;  // Mentorluk yapılıyorsa, mentee ismi
    private String description;
    private boolean isCompleted;
    private boolean isActive;
    private List<CompletePhaseDTO> phase;
}
