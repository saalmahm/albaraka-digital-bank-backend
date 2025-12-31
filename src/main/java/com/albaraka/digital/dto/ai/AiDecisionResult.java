package com.albaraka.digital.dto.ai;

import com.albaraka.digital.model.enums.AiDecision;

import java.time.LocalDateTime;

public record AiDecisionResult(
        AiDecision decision,
        String comment,
        LocalDateTime evaluatedAt
) { }