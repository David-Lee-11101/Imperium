package com.imperium.api.model;

import com.imperium.domain.model.DocketState;
import com.imperium.domain.model.RoleCode;
import jakarta.validation.constraints.NotNull;

/**
 * 状态推进请求
 */
public record TransitionRequest(

    @NotNull(message = "Target state is required")
    DocketState targetState,

    @NotNull(message = "Actor role is required")
    RoleCode actor,

    String comment
) {
}
