package kwh.Petmily_BE.global.security.config;

import kwh.Petmily_BE.global.security.jwt.JwtFilter;
import kwh.Petmily_BE.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // JwtFilter를 빈으로 주입

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증 예외 경로 등록
        http
                .csrf(csrf -> {
                    // 최신 스프링 시큐리티에서 CSRF 토큰을 요청 속성으로 다루도록 설정
                    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                    csrf.csrfTokenRequestHandler(requestHandler)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/reissue", "/", "/swagger-ui/**", "/v3/**", "/api/auth/kakao/callback", "/api/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth").authenticated()
                        .anyRequest().authenticated());

        // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 보안 헤더 강화 - 권장된 람다 스타일 사용
        http.headers(headers -> {
            headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; img-src 'self' data:; style-src 'self' 'unsafe-inline';"));
            headers.frameOptions(frame -> frame.deny());
        });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프로퍼티에서 허용 origin 로드 (패턴 사용 허용)
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "X-Requested-With", "Content-Type", "X-CSRF-TOKEN", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        // 프론트에서 읽어야 할 헤더가 있다면 추가 (예: Authorization 등)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-XSRF-TOKEN"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            BCryptPasswordEncoder passwordEncoder,
            CustomUserDetailsService customUserDetailsService // 또는 필드로 주입받은 것을 사용할 수도 있음
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // 💡 CustomUserDetailsService 설정 확인
        provider.setPasswordEncoder(passwordEncoder);             // 💡 PasswordEncoder 설정 확인
        return provider;
    }
}
