package com.xhs.filter;

import com.xhs.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

// JWT过滤器
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    private static final Set<String> ALLOWED_PATHS = Set.of("/user/login", "/user/register");
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return ALLOWED_PATHS.contains(request.getServletPath());
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        // 检查 Token 是否存在且以 "Bearer " 开头
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去除 "Bearer " 前缀

            try {
                Integer userId = jwtUtils.getUserId(token); // 解析 Token
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token 无效或已过期");
                return;
            }
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token 不存在或格式错误");
            return;
        }

        chain.doFilter(request, response);
    }


}
