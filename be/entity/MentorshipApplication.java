package com.obss.mentorapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "mentorship_application")
@Data
@Getter
@Setter
public class MentorshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private Date applicationDate;
    private String status; // e.g., "PENDING", "APPROVED", "REJECTED"
    private String description; // Yeni açıklama alanı

    // Getter method for Applicant ID
    public Long getApplicantId() {
        return applicant != null ? applicant.getId() : null;
    }

    // Getter method for Topic ID
    public Long getTopicId() {
        return topic != null ? topic.getId() : null;
    }

    // Getter method for Applicant Email
    public String getApplicantEmail() {
        return applicant != null ? applicant.getEmail() : null;
    }

    // Getter method for Topic Name
    public String getTopicName() {
        return topic != null ? topic.getName() : null;
    }
}
