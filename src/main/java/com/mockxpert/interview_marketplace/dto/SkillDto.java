package com.mockxpert.interview_marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDto {

    private Long skillId;

    @NotBlank(message = "Skill name is required")
    private String name;

    private String description;
}
