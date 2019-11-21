package com.hoioy.springsecurityurlseparate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
//此处配置Order=1，因而比 GroupTwoSecurityConfig 配置类先执行，配置优先级高，因为Spring Security的配置规则是"先执行的优先级高"。
@Order(1)
public class GroupOneSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers("/foo/**")
                .and().authorizeRequests().anyRequest().fullyAuthenticated()
                .and().oauth2ResourceServer().jwt();
    }
}
