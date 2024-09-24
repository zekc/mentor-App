package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.MentorshipApplicationDTO;
import com.obss.mentorapp.entity.MentorshipApplication;
import com.obss.mentorapp.service.MentorshipApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MentorshipApplicationService applicationService;

    @GetMapping("/applications")
    public ResponseEntity<List<MentorshipApplicationDTO>> getAllApplications() {
        List<MentorshipApplicationDTO> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<MentorshipApplication> approveApplication(@PathVariable Long id, @RequestBody String applicantEmail) {
        applicantEmail = applicantEmail.replace("\"", ""); // Eğer string tırnak işaretleriyle geliyorsa temizleyin
        MentorshipApplication application = applicationService.updateStatus(id, "APPROVED", applicantEmail);
        if (application != null) {
            return ResponseEntity.ok(application);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<MentorshipApplication> rejectApplication(@PathVariable Long id, @RequestBody String applicantEmail) {
        applicantEmail = applicantEmail.replace("\"", ""); // Eğer string tırnak işaretleriyle geliyorsa temizleyin
        MentorshipApplication application = applicationService.updateStatus(id, "REJECTED", applicantEmail);
        if (application != null) {
            return ResponseEntity.ok(application);
        }
        return ResponseEntity.notFound().build();
    }
}
