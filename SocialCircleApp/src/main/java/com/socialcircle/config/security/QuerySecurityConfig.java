package com.socialcircle.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(100)
public class QuerySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .requestMatchers()
                .antMatchers("/api/**")
                .and().authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/admin/**").hasAnyAuthority(UserRole.ADMIN.toString())
                .and().csrf().disable().cors().disable();

        http
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAccessExceptionHandler())
                .accessDeniedHandler(new CustomAccessExceptionHandler());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui", "/swagger-resources",
                "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }

}
