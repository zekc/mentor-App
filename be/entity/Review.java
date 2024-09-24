package com.obss.mentorapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private User mentee;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;

    private int rating; // 1-5
    private String comment;
}
