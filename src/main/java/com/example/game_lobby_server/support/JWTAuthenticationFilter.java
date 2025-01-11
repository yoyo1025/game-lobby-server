package com.example.game_lobby_server.support;

import com.example.game_lobby_server.controller.UserForm;
import com.example.game_lobby_server.service.JwtSecretKeyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtSecretKeyService jwtSecretKeyService;

    // コンストラクタにJwtSecretKeyServiceを追加
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   BCryptPasswordEncoder bCryptPasswordEncoder,
                                   JwtSecretKeyService jwtSecretKeyService) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtSecretKeyService = jwtSecretKeyService;

        // ログイン用のpathを変更する
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    }

    // 認証の処理
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        try {
            // requestパラメータからユーザ情報を読み取る
            UserForm userForm = new ObjectMapper().readValue(req.getInputStream(), UserForm.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userForm.getName(),
                            userForm.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 認証に成功した場合の処理
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // JwtSecretKeyServiceから秘密鍵を取得
        String secretKey = jwtSecretKeyService.getSecretKey();

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT") // "typ": "JWT" を追加
                .setSubject(((User) auth.getPrincipal()).getUsername()) // usernameだけを設定する
                .setExpiration(new Date(System.currentTimeMillis() + 28_800_000)) // 8時間
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();

        // JSONレスポンスを作成
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
