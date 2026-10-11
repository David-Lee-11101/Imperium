package com.imperium.api.model;

import com.imperium.domain.model.RoleCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 单个派发项请求
 */
public record DelegationItemRequest(
    @NotNull(message = "Target role is required")
    RoleCode roleCode,

    @NotBlank(message = "Execution objective is required")
    String objective,

    List<String> dependsOn
) {
}
