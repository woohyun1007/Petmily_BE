package kwh.Petmily_BE.token;

import lombok.Value;

public class JwtTokenProvider {
    private final String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }
}
