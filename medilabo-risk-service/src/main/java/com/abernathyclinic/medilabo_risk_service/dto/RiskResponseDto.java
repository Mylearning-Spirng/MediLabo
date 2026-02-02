package com.abernathyclinic.medilabo_risk_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskResponseDto {
    public Long patientId;
    public String risk;
    public int triggerCount;

}
