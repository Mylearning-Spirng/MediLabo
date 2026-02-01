package com.abernathyclinic.medilabo_risk_service.dto;

public class RiskResponseDto {
    public Long patientId;
    public String risk;
    public int triggerCount;

    public RiskResponseDto(Long patientId, String risk, int triggerCount) {
        this.patientId = patientId;
        this.risk = risk;
        this.triggerCount = triggerCount;
    }
}
