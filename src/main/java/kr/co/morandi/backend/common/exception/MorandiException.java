package kr.co.morandi.backend.common.exception;

import kr.co.morandi.backend.common.exception.errorcode.ErrorCode;
import lombok.Getter;
@Getter
public class MorandiException extends RuntimeException {

    private final ErrorCode errorCode;

    public MorandiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MorandiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
