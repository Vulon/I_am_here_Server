package com.I_am_here.Configuration;


import com.I_am_here.Security.Token_AuthenticationProvider;
import com.I_am_here.Services.SecretDataLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String MANAGER_ROLE = "ROLE_MANAGER";

    Token_AuthenticationProvider authenticationProvider;

    SecretDataLoader secretDataLoader;

    public SecurityConfig(Token_AuthenticationProvider authenticationProvider, SecretDataLoader secretDataLoader) {
        this.authenticationProvider = authenticationProvider;
        this.secretDataLoader = secretDataLoader;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/manager/*").access("hasRole('ROLE_MANAGER')")
                .antMatchers("/protected/*").access("hasRole('ROLE_MANAGER')")
        .anyRequest().permitAll()
        .and().formLogin().loginPage("/login.html").loginProcessingUrl("/login/auth")
                .defaultSuccessUrl("/homepage.html",true)
        .and().logout().logoutUrl("/login/logout").deleteCookies("JSESSIONID")
        .and().rememberMe().key(secretDataLoader.getSpring_security_key()).tokenValiditySeconds((int)secretDataLoader.getRefreshTokenValidity());
    }
}
