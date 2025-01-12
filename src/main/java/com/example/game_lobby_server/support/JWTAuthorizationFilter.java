package com.example.game_lobby_server.support;

import com.example.game_lobby_server.dto.ResponseDto;
import com.example.game_lobby_server.service.JwtSecretKeyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtSecretKeyService jwtSecretKeyService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JwtSecretKeyService jwtSecretKeyService) {
        super(authenticationManager);
        this.jwtSecretKeyService = jwtSecretKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        // Authorization ヘッダーがない場合もエラーを返す
        if (header == null || !header.startsWith("Bearer ")) {
            ResponseDto responseDto = new ResponseDto("Authorization header is missing or invalid", "error");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            res.setContentType("application/json");
            res.getWriter().write(convertToJson(responseDto));  // JSON形式でレスポンスを返す
            return;
        }

        // JWT認証を試みる
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        if (authentication == null) {
            // 認証に失敗した場合、エラーメッセージをレスポンスにセット
            ResponseDto responseDto = new ResponseDto("Invalid token", "error");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            res.setContentType("application/json");
            res.getWriter().write(convertToJson(responseDto));  // JSON形式でレスポンスを返す
            return;
        }

        // 認証が成功した場合
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            try {
                // JwtSecretKeyService を使用してシークレットキーを取得
                String secret = jwtSecretKeyService.getSecretKey();

                String user = Jwts.parser()
                        .setSigningKey(secret.getBytes())
                        .parseClaimsJws(token.replace("Bearer ", ""))
                        .getBody()
                        .getSubject();

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            } catch (SignatureException e) {
                // JWTの署名が一致しない場合
                if (request instanceof HttpServletResponse) {
                    HttpServletResponse res = (HttpServletResponse) request;
                    ResponseDto responseDto = new ResponseDto("JWT signature does not match locally computed signature", "error");
                    try {
                        responseDto.setMessage(e.getMessage());  // 例外メッセージを追加
                        responseDto.setStatus("error");
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                        res.setContentType("application/json");
                        res.getWriter().write(convertToJson(responseDto));  // JSON形式でレスポンスを返す
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String convertToJson(ResponseDto responseDto) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(responseDto);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";  // 失敗した場合は空の JSON
        }
    }
}
