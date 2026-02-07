package com.abernathyclinic.medilabo_risk_service.controller;

import com.abernathyclinic.medilabo_risk_service.dto.RiskResponseDto;
import com.abernathyclinic.medilabo_risk_service.service.RiskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskController.class)
// ✅ This disables Spring Security filters for this test slice
@AutoConfigureMockMvc(addFilters = false)
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskService riskService;

    @Test
    void getRisk_returnsRiskResponseDto_andForwardsAuthHeader() throws Exception {
        Long patientId = 1L;
        String auth = "Bearer abc";

        RiskResponseDto dto = new RiskResponseDto(patientId, "None", 0);

        Mockito.when(riskService.assess(eq(patientId), eq(auth)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/risk/{patientId}", patientId)
                        .header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.riskLevel").value("None"))
                .andExpect(jsonPath("$.triggerCount").value(0));
    }

    @Test
    void getRisk_worksEvenWithoutAuthHeader() throws Exception {
        Long patientId = 2L;

        RiskResponseDto dto = new RiskResponseDto(patientId, "Borderline", 2);

        Mockito.when(riskService.assess(eq(patientId), eq(null)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/risk/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(2))
                .andExpect(jsonPath("$.riskLevel").value("Borderline"))
                .andExpect(jsonPath("$.triggerCount").value(2));
    }
}
