package kwh.Petmily_BE.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

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
    public String createToken(String username, List<String> roles) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim("username", username)
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredMs))
                .signWith(this.secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 유저 이름 추출(loginId)
    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // 토큰에서 역할 추출
    public List<String> getRoles(String token) {

        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    public Boolean isExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

