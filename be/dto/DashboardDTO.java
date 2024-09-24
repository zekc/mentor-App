package com.obss.mentorapp.dto;

import com.obss.mentorapp.dto.CourseDTO;
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
public class DashboardDTO {
    private String userName;
    private String email;
    private List<String> roles;
    private List<CourseDTO> mentorCourses;
    private List<CourseDTO> menteeCourses;
}
