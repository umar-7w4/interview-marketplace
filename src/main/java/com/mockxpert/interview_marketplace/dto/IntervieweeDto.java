package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntervieweeDto {

    private Long intervieweeId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String educationLevel;

    private List<String> languagesSpoken;

    private String currentRole;

    private String fieldOfInterest;

    private String resume;  // URL or path to the resume file

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private List<Long> skillIds;
}
