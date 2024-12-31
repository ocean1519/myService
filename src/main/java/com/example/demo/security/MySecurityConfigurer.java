package com.example.demo.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class MySecurityConfigurer {

    @Resource
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Resource
    private UserDetailsService customUserDetailsService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public MobilecodeAuthenticationProvider mobilecodeAuthenticationProvider() {
        MobilecodeAuthenticationProvider mobilecodeAuthenticationProvider = new MobilecodeAuthenticationProvider();
        mobilecodeAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        return mobilecodeAuthenticationProvider;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }

    /**
     * 定义认证管理器AuthenticationManager
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(mobilecodeAuthenticationProvider());
        authenticationProviders.add(daoAuthenticationProvider());
        return new ProviderManager(authenticationProviders);

    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(new AntPathRequestMatcher("/login/**")).permitAll()
                                .requestMatchers("/connect/**",  "/send", "/index.html").permitAll()
                                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                             .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                //用于无状态的服务,不依赖服务的存储
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configure -> {
                    //未认证请求受保护资源时处理
                    configure.authenticationEntryPoint(myAuthenticationEntryPoint);
                })
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
