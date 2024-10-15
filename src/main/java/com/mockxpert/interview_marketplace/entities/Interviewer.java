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
@Table(name = "interviewers")
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 500)
    private String bio;

    @Column(name = "current_company")
    private String currentCompany;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @ElementCollection
    @CollectionTable(name = "interviewer_languages", joinColumns = @JoinColumn(name = "interviewer_id"))
    @Column(name = "language")
    private List<String> languagesSpoken;

    @ElementCollection
    @CollectionTable(name = "interviewer_certifications", joinColumns = @JoinColumn(name = "interviewer_id"))
    @Column(name = "certification")
    private List<String> certifications;

    @Column(name = "session_rate", nullable = false)
    private Double sessionRate;

    @Column(nullable = false)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "average_rating", columnDefinition = "Decimal(2,1) default '0.0'")
    private Double averageRating;

    @Column(name = "profile_completion_status", nullable = false)
    private Boolean profileCompletionStatus;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interview> interviews;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Availability> availabilities;

    @OneToOne(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InterviewerVerification interviewerVerification;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "interviewer_skills",
        joinColumns = @JoinColumn(name = "interviewer_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;
    
    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING_VERIFICATION
    }

}
