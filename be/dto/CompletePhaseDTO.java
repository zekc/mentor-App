package com.obss.mentorapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletePhaseDTO {
    private Long id;  // Faz için benzersiz kimlik
    private String phaseName;  // Fazın ismi
    private String status;
    private Boolean completed;
    private LocalDate endDate;
    private Integer rating;
    private String evaluation;
    private Long courseId;
}

