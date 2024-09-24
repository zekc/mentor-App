package com.obss.mentorapp.controller;

import com.obss.mentorapp.dto.MentorshipApplicationDTO;
import com.obss.mentorapp.entity.MentorshipApplication;
import com.obss.mentorapp.mapper.MentorshipApplicationMapper;
import com.obss.mentorapp.service.MentorshipApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentorship-applications")
public class MentorshipApplicationController {

    @Autowired
    private MentorshipApplicationService applicationService;

    @Autowired
    private MentorshipApplicationMapper applicationMapper;

    @GetMapping
    public List<MentorshipApplicationDTO> getAllApplications() {
        List<MentorshipApplication> applications = applicationService.findAll();
        return applications.stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public MentorshipApplicationDTO createApplication(@RequestBody MentorshipApplicationDTO applicationDTO) {
        MentorshipApplication application = applicationMapper.toEntity(applicationDTO);
        MentorshipApplication savedApplication = applicationService.save(application);
        return applicationMapper.toDTO(savedApplication);
    }

    @GetMapping("/{id}")
    public MentorshipApplicationDTO getApplicationById(@PathVariable Long id) {
        MentorshipApplication application = applicationService.findById(id);
        return applicationMapper.toDTO(application);
    }
}
