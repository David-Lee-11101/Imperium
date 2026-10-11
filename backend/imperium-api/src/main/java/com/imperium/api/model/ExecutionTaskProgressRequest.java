package com.imperium.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 执行任务进度更新请求
 */
public record ExecutionTaskProgressRequest(
    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress cannot be less than 0")
    @Max(value = 100, message = "Progress cannot exceed 100")
    Integer progressPercent,

    String outputSummary
) {
}
