package com.obss.mentorapp.service;

import com.obss.mentorapp.dto.CompletePhaseDTO;
import com.obss.mentorapp.entity.Phase;
import com.obss.mentorapp.repository.CourseRepository;
import com.obss.mentorapp.repository.PhaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhaseService {

    private final PhaseRepository phaseRepository;
    private final CourseRepository courseRepository;

    public List<Phase> findAll() {
        return phaseRepository.findAll();
    }

    public Phase addPhase(CompletePhaseDTO phase) {
        var course = courseRepository.findById(phase.getCourseId())
                .orElseThrow(() -> new RuntimeException("course.not.found"));

        var newPhaseEntity = new Phase();
        newPhaseEntity.setPhaseName(phase.getPhaseName());  // Ensure phaseName is set
        newPhaseEntity.setEndDate(phase.getEndDate());
        newPhaseEntity.setCourse(course);

        return phaseRepository.save(newPhaseEntity);  // Save the phase, no need to save course again
    }



    public Phase completePhase(Long phaseId, CompletePhaseDTO phaseDetails) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + phaseId));

        phase.setStatus("Tamamlandı");
        phase.setCompleted(true);
        phase.setRating(phaseDetails.getRating());
        phase.setEvaluation(phaseDetails.getEvaluation());

        return phaseRepository.save(phase);
    }

    public Phase ratePhase(Long phaseId, Phase phaseDetails) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + phaseId));

        phase.setRating(phaseDetails.getRating());
        phase.setEvaluation(phaseDetails.getEvaluation());

        return phaseRepository.save(phase);
    }

    public List<CompletePhaseDTO> getPhasesByCourseId(Long courseId) {
        List<Phase> phases = phaseRepository.findByCourse_Id(courseId);
        return phases.stream().map(phase -> CompletePhaseDTO.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName() != null ? phase.getPhaseName() : "Faz adı mevcut değil")
                .status(phase.getStatus() != null ? phase.getStatus() : "Durum mevcut değil")
                .completed(phase.isCompleted())
                .endDate(phase.getEndDate())
                .rating(phase.getRating() != null ? phase.getRating() : 0)  // Varsayılan 0
                .evaluation(phase.getEvaluation() != null ? phase.getEvaluation() : "Henüz değerlendirme yapılmamış")
                .courseId(phase.getCourse().getId())
                .build()).collect(Collectors.toList());
    }

    public void deletePhase(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + phaseId));

        phaseRepository.delete(phase);  // Fazı veritabanından sil
    }

}


