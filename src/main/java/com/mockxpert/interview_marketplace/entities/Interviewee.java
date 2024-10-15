package com.mockxpert.interview_marketplace.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interviewees")
public class Interviewee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intervieweeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "education_level")
    private String educationLevel;

    @ElementCollection
    @CollectionTable(name = "interviewee_languages", joinColumns = @JoinColumn(name = "interviewee_id"))
    @Column(name = "language")
    private List<String> languagesSpoken;

    @Column(name = "current_job_role")
    private String currentJobRole;

    @Column(name = "field_of_interest")
    private String fieldOfInterest;

    @Column(name = "resume")
    private String resume;  

    @Column(nullable = false)
    private String timezone;

    @OneToMany(mappedBy = "interviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "interviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interview> interviews;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "interviewee_skills",
        joinColumns = @JoinColumn(name = "interviewee_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;
}
