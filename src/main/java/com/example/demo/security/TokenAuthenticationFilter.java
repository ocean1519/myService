package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@WebFilter
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest servletRequest, @NonNull HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException, ServletException {
        String token = getToken(servletRequest);
        // 如果没有token，跳过该过滤器
        if (StringUtils.hasText(token)) {
            // 模拟redis中的数据
            Map<String, CustomUserDetails> map = new HashMap<>();
            //这里放入了两个示例token 仅供测试
            map.put("test_token1", new CustomUserDetails("admin", new BCryptPasswordEncoder().encode("123456"), AuthorityUtils.createAuthorityList("common", "xxl-job")));
            map.put("test_token2", new CustomUserDetails("root", new BCryptPasswordEncoder().encode("123456"), AuthorityUtils.createAuthorityList("common")));

            // 这里模拟从redis获取token对应的用户信息
            CustomUserDetails customUserDetail = map.get(token);
            if (customUserDetail != null) {
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(customUserDetail, null, customUserDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authRequest);
            }
        }
        filterChain.doFilter(servletRequest, httpServletResponse);

    }

    /**
     * 从请求中获取token
     * @param servletRequest 请求对象
     * @return 获取到的token值 可以为null
     */
    private String getToken(HttpServletRequest servletRequest) {
        //先从请求头中获取
        String headerToken = servletRequest.getHeader("Authorization");
        if(StringUtils.hasText(headerToken)) {
            return headerToken;
        }
        //再从请求参数里获取
        String paramToken = servletRequest.getParameter("accessToken");
        if(StringUtils.hasText(paramToken)) {
            return paramToken;
        }
        return null;

    }
}
