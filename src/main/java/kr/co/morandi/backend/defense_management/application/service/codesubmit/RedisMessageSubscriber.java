package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.application.port.out.defensemessaging.DefenseMessagePort;
import kr.co.morandi.backend.defense_management.application.response.codesubmit.CodeResponse;
import kr.co.morandi.backend.defense_management.application.response.codesubmit.MessageResponse;
import kr.co.morandi.backend.defense_management.infrastructure.exception.RedisMessageErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final DefenseMessagePort defenseMessagePort;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String resultString = new String(message.getBody());
            MessageResponse messageResponse = objectMapper.readValue(resultString, MessageResponse.class);
            String jsonMessage = objectMapper.writeValueAsString(CodeResponse.create(messageResponse));
            defenseMessagePort.sendMessage(Long.valueOf(messageResponse.getSseId()), jsonMessage);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new MorandiException(RedisMessageErrorCode.MESSAGE_PARSE_ERROR);
        }
    }
}
