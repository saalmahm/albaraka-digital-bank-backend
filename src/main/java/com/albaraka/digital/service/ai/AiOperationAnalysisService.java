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

    // Seuil métier principal : 10 000 DH
    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    /**
     * Ce service représente une IA simulée qui sert uniquement à analyser une opération bancaire
     * et à fournir une recommandation de risque, et non une décision finale.
     * Pour l’instant, l’analyse est basée sur des règles simples liées au montant afin de préparer
     * l’intégration future de Spring AI. Les opérations de faible montant sont considérées comme
     * peu risquées, tandis que les montants plus élevés déclenchent une recommandation de contrôle
     * humain ou un rejet indicatif. La validation définitive de l’opération reste toujours du
     * ressort du workflow métier ou de l’agent bancaire, ce qui permet de conserver la sécurité,
     * la traçabilité et la conformité aux pratiques bancaires réelles.
     */
    public AiDecisionResult analyze(Operation operation) {

        BigDecimal amount = operation.getAmount();

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
                - Fournir une recommandation de risque (APPROVE, REJECT, NEED_HUMAN_REVIEW),
                  sans prendre la décision finale.
                """
                .formatted(
                        operation.getId(),
                        operation.getType(),
                        amount,
                        operation.getStatus()
                );

        log.info("AI_PROMPT_MOCK - {}", promptText);

        AiDecision decision;
        String comment;

        if (amount.compareTo(THRESHOLD) <= 0) {
            decision = AiDecision.APPROVE;
            comment = "Montant ≤ 10 000 DH : opération considérée comme faible risque (IA simulée).";
        } else if (amount.compareTo(new BigDecimal("50000")) > 0) {
            decision = AiDecision.REJECT;
            comment = "Montant très élevé (> 50 000 DH) : risque jugé critique, rejet fortement recommandé (IA simulée, décision finale par l'agent).";
        } else {
            decision = AiDecision.NEED_HUMAN_REVIEW;
            comment = "Montant entre 10 000 et 50 000 DH : revue humaine recommandée (IA simulée).";
        }

        AiDecisionResult result = new AiDecisionResult(
                decision,
                comment,
                LocalDateTime.now()
        );

        log.info(
                "AI_ANALYSIS_MOCK - operationId={}, amount={}, decision={}, comment={}",
                operation.getId(),
                amount,
                result.decision(),
                result.comment()
        );

        return result;
    }
}