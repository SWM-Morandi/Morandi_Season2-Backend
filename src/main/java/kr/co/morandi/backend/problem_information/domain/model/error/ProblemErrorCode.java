package kr.co.morandi.backend.problem_information.domain.model.error;

import kr.co.morandi.backend.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ProblemErrorCode implements ErrorCode {
    PROBLEM_TIER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시작 티어가 끝 티어보다 높을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
