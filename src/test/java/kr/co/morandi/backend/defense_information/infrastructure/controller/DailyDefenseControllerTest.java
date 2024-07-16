package kr.co.morandi.backend.defense_information.infrastructure.controller;

import kr.co.morandi.backend.ControllerTestSupport;
import kr.co.morandi.backend.defense_information.application.dto.response.DailyDefenseInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DailyDefenseControllerTest extends ControllerTestSupport {
    @DisplayName("DailyDefense 정보를 로그인하지 않은 상태에서 가져올 수 있다.")
    @Test
    void getDailyDefenseInfo() throws Exception {
        // given
        when(dailyDefenseUseCase.getDailyDefenseInfo(any(), any()))
                .thenReturn(DailyDefenseInfoResponse.builder()
                        .problems(List.of())
                        .defenseName("test")
                        .attemptCount(1L)
                        .problemCount(5)
                        .build());

        // when
        final ResultActions perform = mockMvc.perform(get("/daily-defense"));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problems").isArray())
                .andExpect(jsonPath("$.defenseName").isString())
                .andExpect(jsonPath("$.attemptCount").isNumber())
                .andExpect(jsonPath("$.problemCount").isNumber());

    }

}