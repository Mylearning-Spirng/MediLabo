package com.abernathyclinic.medilabo_risk_service.controller;

import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import com.abernathyclinic.medilabo_risk_service.service.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private static final Logger logger = LoggerFactory.getLogger(RiskController.class);

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
        logger.info("Received risk assessment request for patientId={}", patientId);
        RiskResponseDto resp = riskService.assess(patientId, authorizationHeader);
        logger.debug("Assessment completed for patientId={}: {}", patientId, resp);
        return resp;
    }
}