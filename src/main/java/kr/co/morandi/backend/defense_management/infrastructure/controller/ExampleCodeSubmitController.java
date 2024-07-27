package kr.co.morandi.backend.defense_management.infrastructure.controller;

import kr.co.morandi.backend.defense_management.application.service.codesubmit.ExampleCodeSubmitService;
import kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit.CodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/submit")
public class ExampleCodeSubmitController {

    private final ExampleCodeSubmitService exampleCodeQueueService;

    @PostMapping("/example")
    public ResponseEntity<Void> submit(@RequestBody CodeRequest codeRequest) {
        exampleCodeQueueService.submitCodeToQueue(codeRequest);
        return ResponseEntity.ok().build();
    }
}
