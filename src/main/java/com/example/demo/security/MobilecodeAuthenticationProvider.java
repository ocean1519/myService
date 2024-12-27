package com.example.demo.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class MobilecodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        MobilecodeAuthenticationToken mobilecodeAuthenticationToken = (MobilecodeAuthenticationToken) authentication;
        String phone = mobilecodeAuthenticationToken.getPhone();
        String mobileCode = mobilecodeAuthenticationToken.getMobileCode();
        System.out.println("登陆手机号：" + phone);
        System.out.println("手机验证码：" + mobileCode);

        // 模拟从redis中读取手机号对应的验证码及其用户名
        Map<String, String> dataFromRedis = new HashMap<>();
        dataFromRedis.put("code", "6789");
        dataFromRedis.put("username", "admin");

        // 判断验证码是否一致
        if (!mobileCode.equals(dataFromRedis.get("code"))) {
            throw new BadCredentialsException("验证码错误");
        }

        // 如果验证码一致，从数据库中读取该手机号对应的用户信息
        CustomUserDetails loadedUser = (CustomUserDetails) userDetailsService.loadUserByUsername(dataFromRedis.get("username"));
        if (loadedUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new MobilecodeAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return MobilecodeAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
