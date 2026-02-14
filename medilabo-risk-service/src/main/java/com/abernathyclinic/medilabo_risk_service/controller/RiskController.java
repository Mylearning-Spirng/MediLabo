package com.abernathyclinic.medilabo_risk_service.controller;

import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import com.abernathyclinic.medilabo_risk_service.service.RiskService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * Assess the diabetes risk for a patient based on their medical notes and demographics.
     * @param patientId
     * @param authorizationHeader
     * @return
     */
    @GetMapping("/{patientId}")
    public RiskResponseDto assess(
            @PathVariable Long patientId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        return riskService.assess(patientId, authorizationHeader);
    }
}