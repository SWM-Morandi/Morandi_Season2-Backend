package kr.co.morandi.backend.judgement.domain.model.baekjoon.cookie;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.judgement.domain.error.BaekjoonCookieErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaekjoonCookie {

    @Column(name = "baekjoon_cookie")
    private String value;
    private LocalDateTime expiredAt;

    public boolean isValidCookie(LocalDateTime now) {
        return now.isBefore(expiredAt);
    }

    public static BaekjoonCookie of(String cookie, LocalDateTime nowDateTime) {
        return BaekjoonCookie.builder()
                .cookie(cookie)
                .nowDateTime(nowDateTime)
                .build();
    }

    private void validateCookie(String cookie) {
        if (cookie == null || cookie.trim().isEmpty()) {
            throw new MorandiException(BaekjoonCookieErrorCode.INVALID_COOKIE_VALUE);
        }
    }

    private LocalDateTime calculateCookieExpiredAt(LocalDateTime cookieCreatedAt) {
        return cookieCreatedAt.plusHours(6);
    }

    @Builder
    private BaekjoonCookie(String cookie, LocalDateTime nowDateTime) {
        validateCookie(cookie);
        this.value = cookie;
        this.expiredAt = calculateCookieExpiredAt(nowDateTime);
    }
}
