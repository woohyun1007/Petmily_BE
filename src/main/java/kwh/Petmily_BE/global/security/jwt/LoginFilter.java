package kwh.Petmily_BE.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper; // 💡 ObjectMapper import
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kwh.Petmily_BE.domain.user.dto.LoginRequestDto; // 💡 LoginRequestDto import
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 1. HTTP Body에서 JSON 파싱 (로그인 ID/PW 추출)
        try {
            LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("Login attempt for user: {}", loginRequest.getLoginId());

            // 2. 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getLoginId(),
                    loginRequest.getPassword()
            );

            // 3. AuthenticationManager를 통해 인증 위임
            // 부모 클래스가 설정받은 AuthenticationManager를 사용하여 인증을 시도합니다.
            return this.getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            log.error("Failed to parse login request: {}", e.getMessage());
            // JSON 파싱 실패 시 인증 실패로 간주
            throw new AuthenticationServiceException("Invalid request format", e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        String token = jwtTokenProvider.createToken(authentication);
        log.info("Authentication successful, token generated.");

        // 응답 헤더 설정
        response.addHeader("Authorization", "Bearer " + token);

        // 응답 본문 설정 및 JSON 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String result = String.format("{\"message\": \"success\", \"token\": \"%s\"}", token);
        response.getWriter().write(result);

        log.info("로그인 성공: {}", authentication.getName());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401 error
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"error\": \"로그인에 실패하였습니다. 아이디 또는 비밀번호를 확인해주세요.\"}");
    }
}
