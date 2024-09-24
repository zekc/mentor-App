package com.obss.mentorapp.mapper;

import com.obss.mentorapp.dto.MentorshipApplicationDTO;
import com.obss.mentorapp.entity.MentorshipApplication;
import com.obss.mentorapp.entity.Topic;
import com.obss.mentorapp.entity.User;
import com.obss.mentorapp.service.TopicService;
import com.obss.mentorapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class MentorshipApplicationMapper {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    // DTO'dan Entity'ye dönüştürme
    //oto yapılacak
    //ex özelleştirme yapılacak
    public MentorshipApplication toEntity(MentorshipApplicationDTO dto) {
        if (dto.getApplicantEmail() == null || dto.getApplicantEmail().isEmpty()) {
            throw new RuntimeException("Applicant email is null or empty");
        }

        MentorshipApplication application = new MentorshipApplication();

        // Kullanıcıyı bul
        User applicant = userService.findByEmail(dto.getApplicantEmail())
                .orElseThrow(() -> new RuntimeException("Applicant not found with email: " + dto.getApplicantEmail()));

        // Topic'i isme göre bul
        Topic topic = topicService.findByName(dto.getTopicName())
                .orElseThrow(() -> new RuntimeException("Topic not found with name: " + dto.getTopicName()));
        //chaining setter
        application.setApplicant(applicant);
        application.setTopic(topic);
        application.setApplicationDate(new Date()); // Şu anki tarihi set et
        application.setStatus("PENDING"); // Varsayılan durum "PENDING" olabilir
        application.setDescription(dto.getDescription()); // Açıklamayı set et

        return application;
    }

    // Entity'den DTO'ya dönüştürme
    public MentorshipApplicationDTO toDTO(MentorshipApplication entity) {
        return MentorshipApplicationDTO.builder()
                .id(entity.getId()) // Başvurunun ID'sini ekle
                .applicantEmail(Optional.ofNullable(entity.getApplicant()).map(User::getEmail).orElse("Unknown")) // Kullanıcının email'ini al
                .topicName(Optional.ofNullable(entity.getTopic()).map(Topic::getName).orElse("Unknown")) // Topic'in ismini al
                .applicationDate(entity.getApplicationDate())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .build();
    }
}
