package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import com.amazonaws.services.sqs.model.SendMessageResult;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;

public interface MessagingQueueService {
    public SendMessageResult sendMessage(CodeRequest codeRequest);
}
