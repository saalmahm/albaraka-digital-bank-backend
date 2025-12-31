package com.albaraka.digital.service.ai;

import com.albaraka.digital.dto.ai.AiDecisionResult;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.model.enums.AiDecision;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AiOperationAnalysisService {

    // Seuil métier : 10 000 DH
    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    /**
     * Analyse "IA simulée" d'une opération bancaire.
     * elle applique une logique simple basée sur le montant.
     */
    public AiDecisionResult analyze(Operation operation) {

        BigDecimal amount = operation.getAmount();

        // 1) Construction d'un "prompt" explicatif (contexte bancaire)
        String promptText = """
                Contexte bancaire - Analyse d'opération sensible

                Règle métier :
                - Montant ≤ 10 000 DH : opération validée automatiquement.
                - Montant > 10 000 DH : opération PENDING avec justificatif obligatoire
                  et analyse intelligente.

                Données de l'opération :
                - ID          : %d
                - Type        : %s
                - Montant     : %s DH
                - Statut      : %s

                Objectif :
                - Décider si l'opération doit être APPROVE, REJECT ou NEED_HUMAN_REVIEW.
                """.formatted(
                operation.getId(),
                operation.getType(),
                amount,
                operation.getStatus()
        );

        // On log le "prompt" pour traçabilité (même sans LLM)
        log.info("AI_PROMPT - {}", promptText);

        // 2) Logique de décision simulée
        AiDecision decision;
        String comment;

        if (amount.compareTo(THRESHOLD) <= 0) {
            // Cas normalement déjà géré en validation automatique,
            // mais on garde une décision IA cohérente.
            decision = AiDecision.APPROVE;
            comment = "Montant ≤ 10 000 DH : opération considérée comme faible risque (IA simulée).";
     } else if (amount.compareTo(new BigDecimal("50000")) > 0) {
            // Au-delà de 50 000 DH, l'IA considère le risque très élevé
            // et recommande fortement un rejet, sous réserve de validation humaine.
            decision = AiDecision.REJECT;
            comment = "Montant très élevé (> 50 000 DH) : risque jugé critique, rejet fortement recommandé (IA simulée, décision finale par l'agent).";
        } else {
            // Entre 10 000 et 50 000 DH : demande de revue humaine
            decision = AiDecision.NEED_HUMAN_REVIEW;
            comment = "Montant entre 10 000 et 50 000 DH : revue humaine recommandée (IA simulée).";
        }

        AiDecisionResult result = new AiDecisionResult(
                decision,
                comment,
                LocalDateTime.now()
        );

        // 3) Logging métier de la décision IA
        log.info(
                "AI_ANALYSIS - operationId={}, amount={}, decision={}, comment={}",
                operation.getId(),
                amount,
                result.decision(),
                result.comment()
        );

        return result;
    }
}