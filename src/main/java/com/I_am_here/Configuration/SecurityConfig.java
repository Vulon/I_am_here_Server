package com.I_am_here.Configuration;


import com.I_am_here.Security.TokenFilter;
import com.I_am_here.Security.Token_AuthenticationProvider;
import com.I_am_here.Services.SecretDataLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        http
                .csrf().disable()
                .addFilterAfter(new TokenFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/web/*").access("hasAuthority('ACCOUNT_MANAGER')")
                .antMatchers("/protected/*").access("hasAuthority('ACCOUNT_MANAGER')")
                .antMatchers("/app/host/*").access("hasAuthority('ACCOUNT_HOST')")
                .antMatchers("/app/participator/*").access("hasAuthority('ACCOUNT_PARTICIPATOR')")
        .and().formLogin().loginPage("/login.html").loginProcessingUrl("/web/auth")
                .defaultSuccessUrl("/homepage.html",true)
        .and().logout().logoutUrl("/web/logout").deleteCookies("JSESSIONID");
        //.and().rememberMe().key(secretDataLoader.getSpring_security_key()).tokenValiditySeconds((int)secretDataLoader.getRefreshTokenValidity());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login.html", "/web/logout", "/app/register", "/app/login", "/web/register", "/web/login");

    }
}
