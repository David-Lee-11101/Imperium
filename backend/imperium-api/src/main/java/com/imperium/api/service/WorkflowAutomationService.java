package com.imperium.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.imperium.api.exception.DocketException;
import com.imperium.api.model.CaesarDecisionRequest;
import com.imperium.api.model.CreateDelegationRequest;
import com.imperium.api.model.DelegationItemRequest;
import com.imperium.api.model.ExecutionTaskCompleteRequest;
import com.imperium.api.model.TransitionRequest;
import com.imperium.api.model.AuditActionRequest;
import com.imperium.domain.entity.DocketEntity;
import com.imperium.domain.entity.TribuneReviewEntity;
import com.imperium.domain.entity.SenateSessionEntity;
import com.imperium.domain.entity.ExecutionTaskEntity;
import com.imperium.domain.mapper.DocketMapper;
import com.imperium.domain.mapper.SenateSessionMapper;
import com.imperium.domain.mapper.TribuneReviewMapper;
import com.imperium.domain.mapper.ExecutionTaskMapper;
import com.imperium.domain.model.DocketState;
import com.imperium.domain.model.OperatingMode;
import com.imperium.domain.model.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统自动编排服务
 */
@Service
@RequiredArgsConstructor
public class WorkflowAutomationService {

    private final DocketMapper docketMapper;
    private final SenateSessionMapper senateSessionMapper;
    private final TribuneReviewMapper tribuneReviewMapper;
    private final ExecutionTaskMapper executionTaskMapper;
    private final DocketService docketService;
    private final SenateService senateService;
    private final ExecutionTaskService executionTaskService;
    private final AuditArchiveService auditArchiveService;

    @Transactional
    public void autoTriage(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.EDICT_ISSUED) {
            return;
        }

        docket.setSummary(buildSummary(docket.getEdictRaw()));
        docketMapper.updateById(docket);

        docketService.transition(docketId, new TransitionRequest(DocketState.TRIAGED, RoleCode.PRAECO, "Automated Praeco triage and normalization completed"));
        if (docket.getMode() == OperatingMode.DIRECT_DECREE) {
            docketService.transition(docketId, new TransitionRequest(DocketState.AWAITING_CAESAR, RoleCode.PRAECO, "Direct-decree mode bypassed the Senate"));
        } else {
            docketService.transition(docketId, new TransitionRequest(DocketState.IN_SENATE, RoleCode.PRAECO, "Docket added to the Senate agenda"));
            senateService.openSession(docketId);
        }
    }

    @Transactional
    public void finalizeSenate(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.DEBATING && docket.getState() != DocketState.IN_SENATE) {
            return;
        }

        SenateSessionEntity session = senateSessionMapper.selectOne(
            new LambdaQueryWrapper<SenateSessionEntity>()
                .eq(SenateSessionEntity::getDocketId, docketId)
                .eq(SenateSessionEntity::getStatus, "CLOSED")
                .orderByDesc(SenateSessionEntity::getOpenedAt)
                .last("LIMIT 1")
        );
        if (session == null) {
            return;
        }

        docketService.transition(docketId, new TransitionRequest(DocketState.VETO_REVIEW, RoleCode.TRIBUNE, "Senate session closed; Tribune review started"));
    }

    @Transactional
    public void autoCaesarApprove(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.AWAITING_CAESAR) {
            return;
        }

        TribuneReviewEntity review = tribuneReviewMapper.selectOne(
            new LambdaQueryWrapper<TribuneReviewEntity>()
                .eq(TribuneReviewEntity::getDocketId, docketId)
                .orderByDesc(TribuneReviewEntity::getCreatedAt)
                .last("LIMIT 1")
        );
        if (review == null || "REJECTED".equals(review.getReviewResult())) {
            return;
        }

        docketService.caesarApprove(docketId, new CaesarDecisionRequest(
            "Automation policy approved execution after Tribune review",
            null
        ));
        autoDelegate(docketId);
    }

    @Transactional
    public void autoDelegate(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.MANDATED) {
            return;
        }

        if (!docketService.listDelegations(docketId).isEmpty()) {
            return;
        }

        String objective = docket.getSummary() != null && !docket.getSummary().isBlank()
            ? docket.getSummary()
            : docket.getTitle();

        docketService.createDelegations(docketId, new CreateDelegationRequest(java.util.List.of(
            new DelegationItemRequest(RoleCode.LEGATUS, "Execute primary objective: " + objective, java.util.List.of())
        )));
    }

    @Transactional
    public void autoCompleteExecution(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.DELEGATED && docket.getState() != DocketState.IN_EXECUTION) {
            return;
        }

        var tasks = executionTaskMapper.selectList(
            new LambdaQueryWrapper<ExecutionTaskEntity>()
                .eq(ExecutionTaskEntity::getDocketId, docketId)
                .ne(ExecutionTaskEntity::getStatus, "COMPLETED")
        );

        for (ExecutionTaskEntity task : tasks) {
            executionTaskService.complete(task.getId(), new ExecutionTaskCompleteRequest("Automation policy completed the execution task"));
        }
    }

    @Transactional
    public void autoPassAudit(String docketId) {
        DocketEntity docket = requireDocket(docketId);
        if (docket.getState() != DocketState.UNDER_AUDIT) {
            return;
        }

        auditArchiveService.pass(docketId, new AuditActionRequest(
            java.util.List.of("Automation policy found no additional risk"),
            java.util.List.of("Automation policy passed the audit"),
            "Automation policy completed the audit and archived the docket",
            java.util.List.of("auto-archive")
        ));
    }

    private DocketEntity requireDocket(String docketId) {
        DocketEntity docket = docketMapper.selectById(docketId);
        if (docket == null) {
            throw DocketException.notFound(docketId);
        }
        return docket;
    }

    private String buildSummary(String edictRaw) {
        if (edictRaw == null || edictRaw.isBlank()) {
            return "";
        }
        String normalized = edictRaw.strip().replace('\n', ' ');
        return normalized.length() > 160 ? normalized.substring(0, 160) + "…" : normalized;
    }
}
