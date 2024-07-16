package kr.co.morandi.backend.defense_management.infrastructure.exception;

import kr.co.morandi.backend.common.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SQSMessageErrorCode implements ErrorCode {
    MESSAGE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWS SQS에 보낼 메시지를 파싱하지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
