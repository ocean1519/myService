package com.example.demo.security;

import com.example.demo.entity.Result;
import com.example.demo.utils.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Result<String> result = Result.fail(401, "用户未登录或已过期");
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(JsonUtil.toJson(result));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result<String> result = Result.fail(403, "权限不足");
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(JsonUtil.toJson(result));
    }
}

