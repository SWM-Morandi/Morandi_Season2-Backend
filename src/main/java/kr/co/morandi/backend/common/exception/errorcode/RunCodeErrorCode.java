package kr.co.morandi.backend.common.exception.errorcode;

import kr.co.morandi.backend.common.exception.errorcode.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum RunCodeErrorCode implements ErrorCode {

    RUN_CODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "코드 실행 중 에러가 발생했습니다.");

    private final HttpStatus httpStatus;

    private final String message;
}