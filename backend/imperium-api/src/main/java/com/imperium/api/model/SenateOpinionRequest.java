package com.imperium.api.model;

import com.imperium.domain.model.RoleCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 元老意见提交请求
 */
public record SenateOpinionRequest(
    @NotNull(message = "Senator role is required")
    RoleCode roleCode,

    @NotBlank(message = "Stance is required")
    String stance,

    @NotBlank(message = "Summary is required")
    String summary,

    String details
) {
}
