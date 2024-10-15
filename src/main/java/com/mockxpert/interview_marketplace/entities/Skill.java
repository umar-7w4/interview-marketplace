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
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    // Relationships
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Interviewer> interviewers;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Interviewee> interviewees;
}
