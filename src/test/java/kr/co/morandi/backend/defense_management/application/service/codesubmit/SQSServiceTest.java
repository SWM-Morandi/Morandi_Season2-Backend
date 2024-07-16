package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.IntegrationTestSupport;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSServiceTest extends IntegrationTestSupport {

    @Autowired
    private SQSService sqsService;

    @Autowired
    private AmazonSQS amazonSQS;

    @DisplayName("사용자가 소스코드를 제출하면 AWS SQS에 JSON 메시지가 올바른 주소에 전송된다.")
    @Test
    void correctSendMessage() {
        // given
        CodeRequest 코드_요청_정보 = CodeRequest.create("Hello world", "Python", "", "123");

        SendMessageResult 전송_결과물 = new SendMessageResult().withMessageId("12345");
        when(amazonSQS.sendMessage(any(SendMessageRequest.class))).thenReturn(전송_결과물);

        // when
        SendMessageResult 실제_전송_결과물 = sqsService.sendMessage(코드_요청_정보);

        // then
        assertEquals(전송_결과물, 실제_전송_결과물);
    }
}