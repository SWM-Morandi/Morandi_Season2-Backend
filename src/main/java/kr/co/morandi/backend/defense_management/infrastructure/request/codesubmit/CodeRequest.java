package kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CodeRequest {

    private String code;
    private String language;
    private String input;
    private String sseId;

    public static CodeRequest create(String code, String language, String input, String sseId) {
        return CodeRequest.builder()
                .code(code)
                .language(language)
                .input(input)
                .sseId(sseId)
                .build();
    }
    @Builder
    private CodeRequest(String code, String language, String input, String sseId) {
        this.code = code;
        this.language = language;
        this.input = input;
        this.sseId = sseId;
    }
}
