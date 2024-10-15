package com.mockxpert.interview_marketplace.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerSkillDto {

    private Long interviewerSkillId;

    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @Min(value = 0, message = "Years of experience must be non-negative")
    private int yearsOfExperience;

    @NotBlank(message = "Proficiency level is required")
    private String proficiencyLevel;  // e.g., Beginner, Intermediate, Advanced

    @NotNull(message = "Certification status is required")
    private Boolean certified;
}