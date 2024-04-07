package kr.co.morandi.backend.member_management.application.config.oauth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.common.exception.errorcode.OAuthErrorCode;
import kr.co.morandi.backend.member_management.domain.model.member.Member;
import kr.co.morandi.backend.member_management.domain.model.oauth.constants.Role;
import kr.co.morandi.backend.member_management.domain.model.oauth.constants.TokenType;
import kr.co.morandi.backend.member_management.domain.model.oauth.security.SecurityConstants;
import kr.co.morandi.backend.member_management.domain.model.oauth.response.AuthenticationToken;
import kr.co.morandi.backend.member_management.domain.service.oauth.OAuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import java.security.PrivateKey;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import static kr.co.morandi.backend.member_management.infrastructure.config.oauth.IgnoredURIManager.PATTERN;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtProvider {

    private final SecurityConstants securityConstants;

    private final RedisTemplate<String, String> redisTemplate;

    private final OAuthUserDetailsService oAuthUserDetailsService;
    public AuthenticationToken getAuthenticationToken(Member member) {
        String accessToken = generateAccessToken(member.getMemberId(), Role.USER);
        String refreshToken = generateRefreshToken(member.getMemberId(), Role.USER);
        return AuthenticationToken.create(accessToken, refreshToken);
    }
    private String generateAccessToken(Long id, Role role) {
        final Date issuedAt = new Date();
        final Date accessTokenExpiresIn = new Date(issuedAt.getTime() + securityConstants.ACCESS_TOKEN_EXPIRATION);
        return buildAccessToken(id, issuedAt, accessTokenExpiresIn, role);
    }
    private String generateRefreshToken(Long id, Role role) {
        final Date issuedAt = new Date();
        final Date refreshTokenExpiresIn = new Date(issuedAt.getTime() + securityConstants.REFRESH_TOKEN_EXPIRATION);
        return buildRefreshToken(id, issuedAt, refreshTokenExpiresIn, role);
    }
    private String buildAccessToken(Long id, Date issuedAt, Date expiresIn, Role role) {
        final PrivateKey encodedKey = getPrivateKey();
        return jwtCreate(id, issuedAt, expiresIn, role, encodedKey, TokenType.ACCESS_TOKEN);
    }
    private String buildRefreshToken(Long id, Date issuedAt, Date expiresIn, Role role) {
        final PrivateKey encodedKey = getPrivateKey();
        String refreshToken = jwtCreate(id, issuedAt, expiresIn, role, encodedKey, TokenType.REFRESH_TOKEN);
        saveRefreshTokenToRedis(id, refreshToken);
        return refreshToken;
    }
    private void saveRefreshTokenToRedis(Long id, String refreshToken) {
        String key = "refreshToken_memberId:" + id;
        redisTemplate.opsForValue().set(
                key, // key
                refreshToken, // value
                securityConstants.REFRESH_TOKEN_EXPIRATION, // timeout
                TimeUnit.MILLISECONDS); // unit
    }
    private String jwtCreate(Long id, Date issuedAt, Date expiresIn, Role role,
                             PrivateKey encodedKey, TokenType tokenType) {
        return Jwts.builder()
                .setIssuer("MORANDI")
                .setIssuedAt(issuedAt)
                .setSubject(id.toString())
                .claim("type", tokenType)
                .claim("role", role)
                .setExpiration(expiresIn)
                .signWith(encodedKey)
                .compact();
    }
    private PrivateKey getPrivateKey() {
        return securityConstants.getPrivateKey();
    }
    public boolean validateAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken))
            throw new MorandiException(OAuthErrorCode.INVALID_TOKEN);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(securityConstants.getPublicKey())
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            throw new MorandiException(OAuthErrorCode.INVALID_TOKEN);
        }
    }
    public boolean validateRefreshToken(String refreshToken) {
        Long memberId = getMemberIdFromToken(refreshToken);
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = "refreshToken_memberId:" + memberId.toString();
        Optional<String> maybeStoredToken = Optional.of(valueOps.get(key));
        if (maybeStoredToken.isEmpty())
            return false;
        return refreshToken.equals(maybeStoredToken.get());
    }

    public String reissueAccessToken(String refreshToken) {
        Long memberId = getMemberIdFromToken(refreshToken);
        return generateAccessToken(memberId, Role.USER);
    }
    public Authentication getAuthentication(String accessToken) {
        Long memberId = getMemberIdFromToken(accessToken);
        UserDetails userDetails = oAuthUserDetailsService.loadUserByUsername(memberId.toString());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    private Long getMemberIdFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(securityConstants.getPublicKey())
                .build()
                .parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return Long.parseLong(claims.getSubject());
    }
    public boolean isIgnoredURI(String uri) {
        Matcher matcher = PATTERN.matcher(uri);
        return matcher.find();
    }
    public String getJwtFromRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "accessToken");
        if (cookie != null) {
            return cookie.getValue();
        }
        String accessToken = request.getHeader("Authorization");
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        throw new MorandiException(OAuthErrorCode.TOKEN_NOT_FOUND);
    }
}
