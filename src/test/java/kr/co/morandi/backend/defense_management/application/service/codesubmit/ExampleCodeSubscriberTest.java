package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.IntegrationTestSupport;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.application.port.out.defensemessaging.DefenseMessagePort;
import kr.co.morandi.backend.defense_management.application.response.codesubmit.MessageResponse;
import kr.co.morandi.backend.defense_management.infrastructure.exception.RedisMessageErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisabledIfEnvironmentVariable(named = "redis.enabled", matches = "false")
class ExampleCodeSubscriberTest extends IntegrationTestSupport {

    @MockBean
    private DefenseMessagePort defenseMessagePort;

    @Autowired
    private ExampleCodeSubscriber redisMessageSubscriber;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Redis pub/sub에 메시지가 도착하면 메시지가 정상적으로 SSE에 전송되어야 한다.")
    @Test
    void correctOnMessage() throws JsonProcessingException {
        // given
        MessageResponse messageResponse
                = MessageResponse.create("성공", 0.2, "Hello world", "123");

        String json = objectMapper.writeValueAsString(messageResponse);
        String channel = "channel";
        Message message = new DefaultMessage(channel.getBytes(), json.getBytes());

        when(defenseMessagePort.sendMessage(anyLong(), anyString()))
                .thenReturn(true);

        // when
        redisMessageSubscriber.onMessage(message, null);

        // then
        verify(defenseMessagePort).sendMessage(eq(123L), any(String.class));
    }

    @DisplayName("Redis 메시지 전송 시에 예외가 발생하더라도 3번 이내에 재전송에 성공하면 정상적으로 전송된다.")
    @Test
    void retrySendMessageTest() throws JsonProcessingException {
        // given
        MessageResponse messageResponse
                = MessageResponse.create("성공", 0.2, "Hello world", "123");

        String json = objectMapper.writeValueAsString(messageResponse);
        String channel = "channel";
        Message message = new DefaultMessage(channel.getBytes(), json.getBytes());

        when(defenseMessagePort.sendMessage(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Error"))
                .thenThrow(new RuntimeException("Error"))
                .thenReturn(true);

        // when
        redisMessageSubscriber.onMessage(message, null);

        // then
        verify(defenseMessagePort, times(1 + 2)).sendMessage(eq(123L), any(String.class));
    }

    @DisplayName("Redis 메시지 재전송 로직이 3번 실패하면 예외가 발생한다.")
    @Test
    void retrySendMessageFailTest() throws JsonProcessingException {
        // given
        MessageResponse messageResponse
                = MessageResponse.create("성공", 0.2, "Hello world", "123");

        String json = objectMapper.writeValueAsString(messageResponse);
        String channel = "channel";
        Message message = new DefaultMessage(channel.getBytes(), json.getBytes());

        when(defenseMessagePort.sendMessage(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Error"));

        // when
        MorandiException exception = assertThrows(MorandiException.class,
                () -> redisMessageSubscriber.onMessage(message, null));

        // then
        assertEquals(RedisMessageErrorCode.MESSAGE_SEND_ERROR, exception.getErrorCode());
        verify(defenseMessagePort, times(1 + 3)).sendMessage(eq(123L), any(String.class));
    }

    @DisplayName("Redis pub/sub에 형식에 맞지 않는 메시지가 오면 예외를 발생시켜야 한다.")
    @Test
    void incorrectOnMessage() {
        // when & then
        MorandiException morandiException = assertThrows(MorandiException.class,
                () -> redisMessageSubscriber.onMessage(null, null));
        assertEquals(RedisMessageErrorCode.MESSAGE_PARSE_ERROR, morandiException.getErrorCode());
        assertEquals("Redis의 메시지를 파싱하지 못했습니다.", morandiException.getMessage());
    }
}