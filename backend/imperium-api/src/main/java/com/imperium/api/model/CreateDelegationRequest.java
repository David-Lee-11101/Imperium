package com.imperium.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 执政官批量派发请求
 */
public record CreateDelegationRequest(
    @Valid
    @NotEmpty(message = "At least one delegation is required")
    List<DelegationItemRequest> items
) {
}
