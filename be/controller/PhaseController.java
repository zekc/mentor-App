package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.CompletePhaseDTO;
import com.obss.mentorapp.entity.Phase;
import com.obss.mentorapp.service.PhaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases")
public class PhaseController {

    @Autowired
    private PhaseService phaseService;

    @PostMapping("/add")
    public ResponseEntity<Phase> addPhase(@RequestBody CompletePhaseDTO phase, @RequestParam Long courseId) {
        phase.setCourseId(courseId);
        Phase newPhase = phaseService.addPhase(phase);
        return ResponseEntity.ok(newPhase);
    }

    @PutMapping("/complete/{phaseId}")
    public ResponseEntity<Phase> completePhase(@PathVariable Long phaseId, @RequestBody CompletePhaseDTO phaseDetails) {
        Phase updatedPhase = phaseService.completePhase(phaseId, phaseDetails);
        return ResponseEntity.ok(updatedPhase);
    }

    @PutMapping("/rate/{phaseId}")
    public ResponseEntity<Phase> ratePhase(@PathVariable Long phaseId, @RequestBody Phase phaseDetails) {
        Phase updatedPhase = phaseService.ratePhase(phaseId, phaseDetails);
        return ResponseEntity.ok(updatedPhase);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CompletePhaseDTO>> getPhasesByCourseId(@PathVariable Long courseId) {
        List<CompletePhaseDTO> phases = phaseService.getPhasesByCourseId(courseId);
        return ResponseEntity.ok(phases);
    }

    @DeleteMapping("/delete/{phaseId}")
    public ResponseEntity<Void> deletePhase(@PathVariable Long phaseId) {
        phaseService.deletePhase(phaseId);
        return ResponseEntity.ok().build();  // Başarılı silme işlemi
    }
}

