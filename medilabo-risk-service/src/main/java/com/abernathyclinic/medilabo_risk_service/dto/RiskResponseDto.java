package com.abernathyclinic.medilabo_risk_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskResponseDto {
    private Long patientId;
    private String riskLevel;     // "None", "Borderline", "InDanger", "EarlyOnset"
    private int triggerCount;

}
