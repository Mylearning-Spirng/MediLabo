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

    @GetMapping("/{id}")
    public RiskResponseDto risk(@PathVariable Long id,
                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return riskService.assess(id, auth);
    }
}
