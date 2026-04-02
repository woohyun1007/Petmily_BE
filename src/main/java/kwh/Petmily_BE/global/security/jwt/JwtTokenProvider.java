package kwh.Petmily_BE.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Long expiredMs;
    private final Long refresh_expiredMs;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKeyString, @Value("${spring.jwt.expiration}") Long expiredMs, @Value("${spring.jwt.refresh_expiration}") Long refresh_expiredMs) {
        // 1. SecretKey 초기화 (한 번에 처리)
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

        // 2. 만료 시간 초기화
        this.expiredMs = expiredMs;

        // 3. refresh Token 만료 시간 초기화
        this.refresh_expiredMs = refresh_expiredMs;
    }

    // 토큰 생성
    public String createToken(Authentication authentication) {
        // authentication.getPrincipal()에서 CustomDetails를 꺼냄
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date validity = new Date(now.getTime() + expiredMs);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername()) // loginId
                .claim("id", userDetails.getId())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        // 7일
        Date validity = new Date(now.getTime() + refresh_expiredMs);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("roles", roles)
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
            log.info("Invalid JWT");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT");
        } catch (IllegalArgumentException e) {
            log.info("Illegal JWT");
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 필요한 정보 추출
        Claims claims = getClaims(token);

        Long id = claims.get("id", Long.class);
        String username = claims.getSubject();

        List<?> roles = claims.get("roles", List.class);
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList();
        // 2. 권한 정보 추출 (List -> GrantedAuthority 변환 필요)
        CustomUserDetails userDetails = new CustomUserDetails(id, username, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

