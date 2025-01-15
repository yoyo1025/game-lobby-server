package com.example.game_lobby_server.support;

import com.example.game_lobby_server.service.JwtSecretKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // AuthenticationManager を Bean として定義
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationConfiguration authenticationConfiguration,
                                                   JwtSecretKeyService jwtSecretKeyService) throws Exception {
        // AuthenticationManager を取得
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        http
                .csrf(csrf -> csrf.disable()) // CSRFを無効化
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 最新の CORS 設定
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/signup", "/login", "/make-room", "/rooms", "/rooms/**", "/room-join", "/lobby-websocket/**").permitAll() // signupとloginは誰でもアクセス可能
                        .anyRequest().authenticated() // その他のリクエストは認証が必要
                )
                .addFilter(new JWTAuthenticationFilter(authenticationManager, bCryptPasswordEncoder,jwtSecretKeyService)) // AuthenticationManager を渡す
                .addFilter(new JWTAuthorizationFilter(authenticationManager, jwtSecretKeyService)) // JwtSecretKeyService を渡す
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // セッションをステートレスに設定
                );

        return http.build();
    }


    @Autowired
    public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    // CORS設定を定義
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://172.31.120.116:3000",
                "http://172.31.125.54:3000",
                "http://172.30.161.76:3000",
                "http://172.31.94.191:3000"
        )); // 許可するURL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 許可するHTTPメソッド
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization")); // 許可するヘッダー
        configuration.setAllowCredentials(true); // クッキーを許可
        configuration.setMaxAge(3600L); // 期限は30分とする

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 全てのパスに適用

        return source;
    }
}
