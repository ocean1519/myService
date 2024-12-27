package com.example.demo.controller;

import com.example.demo.entity.Result;
import com.example.demo.exception.ServerException;
import com.example.demo.security.MobilecodeAuthenticationToken;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 用户名密码登录
     * @param username 用户名
     * @param password 密码
     * @return 返回登录结果
     */
    @GetMapping("/usernamePwd")
    public Result<?> usernamePwd(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new ServerException(e.getMessage());
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        return Result.ok(token);
    }

    /**
     * 手机验证码登录
     * @param phone 手机号
     * @param mobileCode 验证码
     * @return 返回登录结果
     */
    @GetMapping("/mobileCode")
    public Result<?> mobileCode(String phone, String mobileCode) {
        MobilecodeAuthenticationToken mobilecodeAuthenticationToken = new MobilecodeAuthenticationToken(phone, mobileCode);
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(mobilecodeAuthenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("验证码错误");
        }
        System.out.println(authenticate);
        String token = UUID.randomUUID().toString().replace("-", "");
        return Result.ok(token);
    }
}
