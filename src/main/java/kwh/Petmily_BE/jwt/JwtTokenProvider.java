package kwh.Petmily_BE.jwt;

import io.jsonwebtoken.*;
import kwh.Petmily_BE.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private SecretKey secretKey;

    public JwtTokenProvider(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 생성
    public String createToken(String username, User.Role role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 유저 이름 추출
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 토큰에서 역할 추출
    public User.Role getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", User.Role.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

//    // 토큰 유효성 검증
//    public boolean validateToken(String token) {
//        try {
//            JwtParser parser = Jwts.parser()
//                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
//                    .build();
//
//            Jws<Claims> claims = parser.parseClaimsJws(token);
//
//            Date now = new Date();
//            return !claims.getBody().getExpiration().before(now);
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
}

