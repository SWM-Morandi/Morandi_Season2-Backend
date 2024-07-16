package kr.co.morandi.backend.defense_management.infrastructure.controller;

import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.ControllerTestSupport;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
class CodeSubmitControllerTest extends ControllerTestSupport {

    @Autowired
    private ObjectMapper objectMapper;
    @DisplayName("소스코드를 제출했을 때 올바르게 전송되며 응답받은 메시지 번호는 일치한다.")
    @Test
    public void testSubmitCodeRequest() throws Exception {
        // Given
        CodeRequest codeRequest = CodeRequest.create("Hello world", "Python", "", "123");
        SendMessageResult sendMessageResult = new SendMessageResult();
        sendMessageResult.setMessageId("12345");
        when(sqsService.sendMessage(any(CodeRequest.class))).thenReturn(sendMessageResult);

        // When & Then
        ResultActions perform = mockMvc.perform(post("/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(codeRequest)));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value("12345"));
    }
}