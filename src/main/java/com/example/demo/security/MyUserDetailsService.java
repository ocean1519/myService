package com.example.demo.security;

import jakarta.annotation.Resource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        CustomUserDetails userDetails;
        // 这里模拟从数据库中获取用户信息
        if (username.equals("admin")) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("common"));
            authorities.add(new SimpleGrantedAuthority("xxl-job"));

            List<GrantedAuthority> myAuth = AuthorityUtils.createAuthorityList("test-user");
            authorities.addAll(myAuth);
            //这里的admin用户拥有common和xxl-job两个权限
            userDetails = new CustomUserDetails("admin", passwordEncoder.encode("123456"), authorities);
            userDetails.setAge(25);
            userDetails.setSex(1);
            userDetails.setAddress("xxxx小区");
            return userDetails;
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
    }
}
