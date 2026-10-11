package com.imperium.api.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 执行任务阻塞请求
 */
public record ExecutionTaskBlockRequest(
    @NotBlank(message = "Block reason is required")
    String blockReason,

    String outputSummary
) {
}
