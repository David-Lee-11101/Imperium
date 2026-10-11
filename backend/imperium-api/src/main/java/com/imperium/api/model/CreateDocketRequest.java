package com.imperium.api.model;

import com.imperium.domain.model.OperatingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建议案请求
 */
public record CreateDocketRequest(

    @NotBlank(message = "Decree content is required")
    String edictRaw,

    @NotNull(message = "Operating mode is required")
    OperatingMode mode,

    String priority
) {
}
