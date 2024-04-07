package kr.co.morandi.backend.common.exception.errorcode;

import kr.co.morandi.backend.common.exception.errorcode.global.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProblemErrorCode implements ErrorCode {

    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;

    private final String message;
}