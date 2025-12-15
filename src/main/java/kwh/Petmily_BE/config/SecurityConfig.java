package kwh.Petmily_BE.config;

import kwh.Petmily_BE.jwt.JwtFilter;
import kwh.Petmily_BE.jwt.JwtTokenProvider;
import kwh.Petmily_BE.jwt.LoginFilter;
import kwh.Petmily_BE.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        AuthenticationManager authenticationManager = new ProviderManager(List.of(daoAuthenticationProvider));

        LoginFilter loginFilter = new LoginFilter(jwtTokenProvider);
        loginFilter.setAuthenticationManager(authenticationManager);
        loginFilter.setFilterProcessesUrl("/login");

        JwtFilter jwtFilter = new JwtFilter(customUserDetailsService, jwtTokenProvider);

        // 인증 예외 경로 등록
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(Basic -> Basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/", "/users/signup", "/swagger-ui/**", "/v3/**").permitAll()
                        .requestMatchers("/users/info").authenticated()
                        .anyRequest().authenticated());

        // 3. Filter 등록
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 세션 설정(Stateless)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 주소 허용
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        // JWT 헤더를 클라이언트가 읽을 수 있게 허용
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

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
