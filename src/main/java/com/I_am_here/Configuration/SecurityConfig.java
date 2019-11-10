package com.I_am_here.Configuration;


import com.I_am_here.Security.TokenFilter;
import com.I_am_here.Security.Token_AuthenticationProvider;
import com.I_am_here.Services.SecretDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig{


    Token_AuthenticationProvider authenticationProvider;

    public SecurityConfig(Token_AuthenticationProvider authenticationProvider, AuthenticationEntryPoint entryPoint) {
        this.authenticationProvider = authenticationProvider;
    }

    @Configuration
    @Order(1)
    public class AppRouteConfig extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            //web.ignoring().antMatchers("/app/register", "/app/login", "/check");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().cors().disable()
                    .addFilterAfter(new TokenFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/app/register", "/app/login", "/check", "/app/logout").permitAll()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/app/host/*").hasAuthority("ACCOUNT_HOST")
                    .antMatchers("/app/participator/*").hasAuthority("ACCOUNT_PARTICIPATOR");
            //.and().requiresChannel().antMatchers("/app/**", "/check").requiresSecure();
        }
    }


    @Configuration
    @Order(2)
    public class WebRouteConfig extends WebSecurityConfigurerAdapter{

        @Autowired
        AuthenticationEntryPoint entryPoint;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            //web.ignoring().antMatchers("/web/register", "/web/login", "/check");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .addFilterAfter(new TokenFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/web/register", "/web/login", "/check")
                    .permitAll()
                    .and().authorizeRequests()
                    .antMatchers("/web/*").hasAuthority("ACCOUNT_MANAGER")
                    .and().httpBasic()
                    .authenticationEntryPoint(entryPoint);
            //.and().requiresChannel().antMatchers("/web/*").requiresSecure();
        }

    }




}
