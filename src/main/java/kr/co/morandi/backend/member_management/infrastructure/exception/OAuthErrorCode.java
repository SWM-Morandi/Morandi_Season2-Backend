package kr.co.morandi.backend.member_management.infrastructure.exception;


import kr.co.morandi.backend.common.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"사용자를 찾을 수 없습니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"인증 시간이 만료된 토큰입니다. 다시 로그인하세요"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "액세스 토큰을 찾을 수 없습니다"),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "액세스 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "리프레시 토큰을 찾을 수 없습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"알 수 없는 오류"),
    GOOGLE_OAUTH_ERROR(HttpStatus.BAD_REQUEST,"구글 OAuth 인증을 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}