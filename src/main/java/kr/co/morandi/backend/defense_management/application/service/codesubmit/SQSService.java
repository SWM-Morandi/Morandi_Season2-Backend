package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.infrastructure.exception.SQSMessageErrorCode;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SQSService {

    @Value("${cloud.aws.sqs.queue.example-compile-url}")
    private String url;

    private final AmazonSQS amazonSQS;

    private final ObjectMapper objectMapper;
    public SendMessageResult sendMessage(CodeRequest codeRequest) {
        try {
            String requestString = objectMapper.writeValueAsString(codeRequest);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(url, requestString);
            return amazonSQS.sendMessage(sendMessageRequest);
        } catch (JsonProcessingException e) {
            throw new MorandiException(SQSMessageErrorCode.MESSAGE_PARSE_ERROR);
        }
    }
}
