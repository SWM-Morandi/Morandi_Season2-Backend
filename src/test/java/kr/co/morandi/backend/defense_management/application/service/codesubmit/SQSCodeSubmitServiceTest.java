package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import kr.co.morandi.backend.IntegrationTestSupport;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.infrastructure.exception.SQSMessageErrorCode;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSCodeSubmitServiceTest extends IntegrationTestSupport {

    @Autowired
    private SQSCodeSubmitService sqsService;

    @MockBean
    private AmazonSQS amazonSQS;

    @DisplayName("사용자가 소스코드를 제출하면 AWS SQS에 JSON 메시지가 올바른 주소에 전송된다.")
    @Test
    void correctSendMessage() {
        // given
        CodeRequest 코드_요청_정보 = CodeRequest.create("Hello world", "Python", "", "123");

        SendMessageResult 전송_결과물 = new SendMessageResult().withMessageId("12345");
        when(amazonSQS.sendMessage(any(SendMessageRequest.class))).thenReturn(전송_결과물);

        // when & then
        assertDoesNotThrow(() -> sqsService.submitCodeToQueue(코드_요청_정보));
    }

    @DisplayName("AWS SQS 메시지 전송 시에 예외가 발생하더라도 3번 이내에 재전송에 성공하면 정상적으로 전송된다.")
    @Test
    void retrySendMessageTest() {
        // given
        CodeRequest 코드_요청_정보 = CodeRequest.create("Hello world", "Python", "", "123");
        SendMessageResult 전송_결과물 = new SendMessageResult().withMessageId("12345");

        when(amazonSQS.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(new RuntimeException("SQS Exception"))
                .thenThrow(new RuntimeException("SQS Exception"))
                .thenReturn(전송_결과물);

        // when & then
        assertDoesNotThrow(() -> sqsService.submitCodeToQueue(코드_요청_정보));
        verify(amazonSQS, times(1 + 2)).sendMessage(any(SendMessageRequest.class));
    }

    @DisplayName("AWS SQS 메시지 재전송 로직이 3번 실패하면 예외가 발생한다.")
    @Test
    void retrySendMessageFailTest() {
        // given
        CodeRequest 코드_요청_정보 = CodeRequest.create("Hello world", "Python", "", "123");

        when(amazonSQS.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(new RuntimeException("SQS Exception"));

        // when & then
        MorandiException exception = assertThrows(MorandiException.class, () -> {
            sqsService.submitCodeToQueue(코드_요청_정보);
        });

        assertEquals(SQSMessageErrorCode.MESSAGE_SEND_FAILED, exception.getErrorCode());
        verify(amazonSQS, times(1 + 3)).sendMessage(any(SendMessageRequest.class));
    }
}