package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.CourseApplicationDTO;
import com.obss.mentorapp.entity.CourseApplication;
import com.obss.mentorapp.mapper.CourseApplicationMapper;
import com.obss.mentorapp.security.UserPrincipal;
import com.obss.mentorapp.service.CourseApplicationService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-applications")
public class CourseApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(CourseApplicationController.class);

    @Autowired
    private CourseApplicationService courseApplicationService;

    @Autowired
    private CourseApplicationMapper courseApplicationMapper;

    // Apply for a course
    @PostMapping
    public ResponseEntity<?> applyForCourse(@RequestBody CourseApplicationDTO applicationDTO) {
        try {
            if (applicationDTO == null || applicationDTO.getCourseId() == null || applicationDTO.getMenteeId() == null) {
                throw new IllegalArgumentException("Course ID and Mentee ID must be provided.");
            }

            logger.info("Received Course ID: {}", applicationDTO.getCourseId());
            logger.info("Received Mentee ID: {}", applicationDTO.getMenteeId());

            CourseApplication application = courseApplicationMapper.toEntity(applicationDTO);
            CourseApplication savedApplication = courseApplicationService.save(application);

            return ResponseEntity.ok(savedApplication);
        } catch (EntityNotFoundException e) {
            logger.error("EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Get pending applications for a mentor
    @GetMapping("/pending/{mentorId}")
    public ResponseEntity<List<CourseApplication>> getPendingApplications(@PathVariable Long mentorId) {
        try {
            List<CourseApplication> pendingApplications = courseApplicationService.getPendingApplicationsByMentor(mentorId);
            if (pendingApplications.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);  // Return 204 if no applications
            }
            return ResponseEntity.ok(pendingApplications);
        } catch (Exception e) {
            logger.error("Error fetching pending applications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all applications for a mentor
    @GetMapping("/mentor/{mentorId}")
    public List<CourseApplication> getApplicationsForMentor(@PathVariable Long mentorId) {
        return courseApplicationService.getApplicationsForMentor(mentorId);
    }

    // Approve an application
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveApplication(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            CourseApplication application = courseApplicationService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Application not found"));
            // Ensure that only the mentor for the course can approve
            if (!application.getCourse().getMentor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to approve this application.");
            }
            courseApplicationService.approveApplication(id);
            return ResponseEntity.status(HttpStatus.OK).body("Application successfully approved.");
        } catch (EntityNotFoundException e) {
            logger.error("Application not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found.");
        } catch (Exception e) {
            logger.error("Error approving application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving application.");
        }
    }

    // Reject an application
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            CourseApplication application = courseApplicationService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Application not found"));

            // Ensure that only the mentor for the course can reject
            if (!application.getCourse().getMentor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to reject this application.");
            }

            courseApplicationService.rejectApplication(id);
            return ResponseEntity.ok("Application successfully rejected.");
        } catch (EntityNotFoundException e) {
            logger.error("Application not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found.");
        } catch (Exception e) {
            logger.error("Error rejecting application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting application.");
        }
    }
}
