package kr.co.morandi.backend.defense_management.application.service.codesubmit;

import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;

public interface ExampleCodeSubmitService {
    void submitCodeToQueue(CodeRequest codeRequest);
}
