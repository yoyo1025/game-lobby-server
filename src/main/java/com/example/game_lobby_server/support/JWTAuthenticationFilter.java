package com.example.game_lobby_server.support;

import com.example.game_lobby_server.controller.UserForm;
import com.example.game_lobby_server.entity.JWTUserDetails;
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
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String secretKey = jwtSecretKeyService.getSecretKey();

        // customUserDetails を取り出す
        JWTUserDetails jwtUserDetails = (JWTUserDetails) auth.getPrincipal();
        Integer userId = jwtUserDetails.getUserId();
        String userName = jwtUserDetails.getUsername();

        System.out.println("JWT userId: " + userId);
        System.out.println("JWT userName: " + userName);

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userName)  // subject に userName を入れる例
                .setExpiration(new Date(System.currentTimeMillis() + 28_800_000))
                // カスタムクレームとして userId, userName を付与
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
