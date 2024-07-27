package kr.co.morandi.backend.defense_management.application.response.codesubmit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CodeResponse {

    private String result;
    private double executeTime;
    private String output;

    public static CodeResponse create(MessageResponse messageResponse) {
        return CodeResponse.builder()
                .result(messageResponse.getResult())
                .executeTime(messageResponse.getExecute_time())
                .output(messageResponse.getOutput())
                .build();
    }
    @Builder
    private CodeResponse(String result, double executeTime, String output) {
        this.result = result;
        this.executeTime = executeTime;
        this.output = output;
    }
}
