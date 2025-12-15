package kwh.Petmily_BE.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kwh.Petmily_BE.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");
        String token = null;

        if(authorization != null && authorization.startsWith("Bearer ")) {
           token = authorization.substring(7);
           log.debug("authorization now");
        }

        // 토큰이 있고, SecurityContext에 인증 정보가 없는 경우에만 인증 시도
        if(token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if(jwtTokenProvider.validateToken(token)) {
                log.info("Auth null");

                String username = jwtTokenProvider.getUsernameFromToken(token);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if(userDetails != null) {
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // SecurityContext에 인증 객체 저장
                    SecurityContextHolder.getContext().setAuthentication(authtoken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
