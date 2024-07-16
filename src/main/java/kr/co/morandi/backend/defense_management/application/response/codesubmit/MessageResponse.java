package kr.co.morandi.backend.defense_management.application.response.codesubmit;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String result;
    private double execute_time;
    private String output;
    private String sseId;

    public static MessageResponse create(String result, double execute_time, String output, String sseId) {
        return MessageResponse.builder()
                .result(result)
                .execute_time(execute_time)
                .output(output)
                .sseId(sseId)
                .build();
    }
}
