package com.abernathyclinic.medilabo_risk_service.controller;

import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import com.abernathyclinic.medilabo_risk_service.service.RiskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/{patientId}")
    public RiskResponseDto getRisk(
            @PathVariable Long patientId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return riskService.calculateRisk(patientId, authorization);
    }
}
