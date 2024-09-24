package com.obss.mentorapp.service;

import com.obss.mentorapp.dto.MentorshipApplicationDTO;
import com.obss.mentorapp.elastic.CourseElasticDTO;
import com.obss.mentorapp.elastic.CourseElasticMapper;
import com.obss.mentorapp.elastic.CourseSearchRepository;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.entity.MentorshipApplication;
import com.obss.mentorapp.mapper.MentorshipApplicationMapper;
import com.obss.mentorapp.repository.MentorshipApplicationRepository;
import com.obss.mentorapp.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(MentorshipApplicationService.class);

    @Autowired
    private MentorshipApplicationRepository mentorshipApplicationRepository;

    @Autowired
    private MentorshipApplicationMapper mentorshipApplicationMapper;

    @Autowired
    private CourseSearchRepository courseSearchRepository;  // Kursları Elasticsearch'e kaydetmek için

    @Autowired
    private CourseRepository courseRepository;  // Kursları kaydetmek için

    @Autowired
    private UserService userService; // Kullanıcı bilgilerine ulaşmak için

    @Autowired
    private TopicService topicService; // Konu bilgilerine ulaşmak için

    public List<MentorshipApplicationDTO> getAllApplications() {
        List<MentorshipApplication> applications = mentorshipApplicationRepository.findAll();
        return applications.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MentorshipApplicationDTO mapToDTO(MentorshipApplication application) {
        String applicantEmail = Optional.ofNullable(application.getApplicant())
                .map(user -> user.getEmail())
                .orElse("Unknown");

        String topicName = Optional.ofNullable(application.getTopic())
                .map(topic -> topic.getName())
                .orElse("Unknown");

        return MentorshipApplicationDTO.builder()
                .id(application.getId())
                .applicantEmail(applicantEmail)
                .topicName(topicName)
                .applicationDate(application.getApplicationDate())
                .status(application.getStatus())
                .description(application.getDescription())
                .build();
    }

    public List<MentorshipApplication> findAll() {
        return mentorshipApplicationRepository.findAll();
    }

    public MentorshipApplication findById(Long id) {
        return mentorshipApplicationRepository.findById(id).orElse(null);
    }

    public MentorshipApplication findByIdAndEmail(Long id, String email) {
        return mentorshipApplicationRepository.findById(id)
                .filter(app -> app.getApplicant() != null && email.equals(app.getApplicant().getEmail()))
                .orElse(null);
    }

    public MentorshipApplication save(MentorshipApplication application) {
        return mentorshipApplicationRepository.save(application);
    }

    public MentorshipApplication updateStatus(Long id, String status, String applicantEmail) {
        MentorshipApplication application = findByIdAndEmail(id, applicantEmail);
        if (application != null) {
            logger.info("Updating status for application ID {}: {} (Applicant Email: {})", id, status, applicantEmail);
            application.setStatus(status);

            // Başvuru onaylandıysa (APPROVED), bu başvuruyu bir kurs olarak kaydet
            if ("APPROVED".equalsIgnoreCase(status)) {
                createCourseFromApplication(application);
            }
            return save(application);
        } else {
            logger.error("Application with ID {} and Applicant Email {} not found. Cannot update status.", id, applicantEmail);
        }

        return null;
    }

    private void createCourseFromApplication(MentorshipApplication application) {
        if (application.getApplicant() == null) {
            logger.error("Application {} has no applicant. Skipping course creation.", application.getId());
            return;
        }

        if (application.getTopic() == null) {
            logger.error("Application {} has no topic. Skipping course creation.", application.getId());
            return;
        }

        String topicName = application.getTopic().getName();
        String mentorUsername = application.getApplicant().getUsername();
        logger.info("Creating course for application {} with topic '{}' by mentor '{}'", application.getId(), topicName, mentorUsername);

        // Kontrol: Aynı mentör, aynı topic altında zaten bir kurs açmış mı?
        boolean mentorHasCourseWithSameTopic = courseRepository.existsByNameAndMentor(application.getTopicName(), application.getApplicant());
        if (mentorHasCourseWithSameTopic) {
            logger.warn("Mentor '{}' already has a course with the topic '{}'. Skipping course creation for application {}", mentorUsername, topicName, application.getId());
            return;
        }

        // Kursu oluşturma
        Course course = new Course();
        course.setName(topicName);
        course.setDescription(application.getDescription());
        course.setMentor(application.getApplicant());  // Başvuran kişi mentor olarak atanıyor
        course.setMentee(null);  // Başlangıçta mentee (öğrenci) yok
        course.setCompleted(false);  // Başlangıçta kurs tamamlanmış olarak işaretlenmiyor

        // Kursu veritabanına kaydet
        courseRepository.save(course);

        // Kursu DTO'ya dönüştür
        CourseElasticMapper CourseMapper = new CourseElasticMapper();
        CourseElasticDTO courseElasticDTO = CourseMapper.toElasticDTO(course);

        // Kursu Elasticsearch'e kaydet
        courseSearchRepository.save(courseElasticDTO);

        logger.info("Course '{}' created successfully for application {}", topicName, application.getId());
    }

    // Eski onaylanan başvurulara dayalı kursları oluştur
    public void createCoursesForApprovedApplications() {
        List<MentorshipApplication> approvedApplications = mentorshipApplicationRepository.findByStatus("APPROVED");
        for (MentorshipApplication application : approvedApplications) {
            createCourseFromApplication(application);
        }
    }
}
