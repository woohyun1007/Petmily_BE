package kwh.Petmily_BE.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kwh.Petmily_BE.domain.user.entity.enums.Role;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Long expiredMs;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKeyString, @Value("${spring.jwt.expiration}") Long expiredMs) {
        // 1. SecretKey 초기화 (한 번에 처리)
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

        // 2. 만료 시간 초기화
        this.expiredMs = expiredMs;
    }

    // 토큰 생성
    public String createToken(Authentication authentication) {
        // authentication.getPrincipal()에서 CustomDetails를 꺼냄
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date validity = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .subject(userDetails.getUsername()) // loginId
                .claim("id", userDetails.getId())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("Invalid JWT");
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT");
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT");
        } catch (IllegalArgumentException e) {
            log.debug("Illegal JWT");
        }
        return false;
    }

    // 토큰에서 유저 ID 추출(Long 타입)
    public Long getUserIdFromToken(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰에서 역할 추출
    @SuppressWarnings("unchecked")
    public Set<Role> getRoles(String token) {

        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", Set.class);
    }
}

