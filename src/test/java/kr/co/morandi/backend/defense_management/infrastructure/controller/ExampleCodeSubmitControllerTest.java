package kr.co.morandi.backend.defense_management.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.ControllerTestSupport;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExampleCodeSubmitControllerTest extends ControllerTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("소스코드를 제출했을 때 정상적인 요청이라면 200 OK 실행이 된다.")
    @Test
    public void testSubmitCodeRequest() throws Exception {
        // given
        CodeRequest codeRequest = CodeRequest.create("Hello world", "Python", "", "123");

        // when & then
        ResultActions perform = mockMvc.perform(post("/submit/example")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(codeRequest)));

        perform.andExpect(status().isOk());
    }

}