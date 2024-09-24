package com.obss.mentorapp.mapper;

import com.obss.mentorapp.dto.CompletePhaseDTO;
import com.obss.mentorapp.entity.Phase;
import org.springframework.stereotype.Component;

@Component
public class PhaseMapper {

    public CompletePhaseDTO toDTO(Phase phase) {
        if (phase == null) {
            return null;
        }

        // Faz DTO'su oluşturulurken eksik alanlar kontrol ediliyor
        return CompletePhaseDTO.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName() != null ? phase.getPhaseName() : "Adı Yok")
                .status(phase.getStatus() != null ? phase.getStatus() : "Tamamlanmadı")
                .completed(phase.isCompleted())
                .endDate(phase.getEndDate() != null ? phase.getEndDate() : null)
                .rating(phase.getRating() != null ? phase.getRating() : 0)
                .evaluation(phase.getEvaluation() != null ? phase.getEvaluation() : "Henüz değerlendirilmedi")
                .courseId(phase.getCourse() != null ? phase.getCourse().getId() : null)
                .build();
    }
}
