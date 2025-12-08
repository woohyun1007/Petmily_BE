package kwh.Petmily_BE.jwt;

import com.fasterxml.jackson.databind.ObjectMapper; // 💡 ObjectMapper import
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kwh.Petmily_BE.dto.users.LoginRequestDto; // 💡 LoginRequestDto import
import kwh.Petmily_BE.dto.users.CustomUserDetails;
import kwh.Petmily_BE.dto.users.LoginResponseDto;
import kwh.Petmily_BE.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 💡 ObjectMapper를 필드에 추가합니다. (Bean으로 등록 후 주입받는 것이 좋으나, 여기서는 간단히 생성)
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            String loginId = loginRequest.loginId();
            String password = loginRequest.password();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing login request body", e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = customUserDetails.getUser();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // GrantedAuthority -> String
                .collect(Collectors.toList());

        String token = jwtTokenProvider.createToken(user.getLoginId(), roles);

        // 응답 DTO 생성
        LoginResponseDto responseDto = LoginResponseDto.of(token, user);

        // 응답 헤더 설정
        response.addHeader("Authorization", "Bearer " + token);

        // 응답 본문 설정 및 JSON 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);  // 200 OK

        // ObjectMapper를 사용하여 DTO를 JSON으로 변환하여 응답 스트림에 쓰기
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), responseDto);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

}
