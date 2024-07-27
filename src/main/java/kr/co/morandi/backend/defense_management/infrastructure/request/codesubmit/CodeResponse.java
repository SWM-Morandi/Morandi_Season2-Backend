package kr.co.morandi.backend.defense_management.infrastructure.request.codesubmit;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CodeResponse {

    private String result;
    private String executeTime;
    private String output;
}
