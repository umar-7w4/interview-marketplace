package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerDto {

    private Long interviewerId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    private String currentCompany;

    private Integer yearsOfExperience;

    private List<String> languagesSpoken;

    private List<String> certifications;

    @NotNull(message = "Session rate is required")
    private Double sessionRate;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private String status;

    private Double averageRating;

    @NotNull(message = "Profile completion status is required")
    private Boolean profileCompletionStatus;

    @NotNull(message = "Verification status is required")
    private Boolean isVerified;

    private List<Long> skillIds;
}

