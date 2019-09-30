package com.I_am_here.Security;

import com.I_am_here.Application;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;



public class Token_Authentication implements Authentication {
    //Stores access token during session.


    private TokenParser tokenParser;



    private String token;

    private Set<SimpleGrantedAuthority> authorities;

    private boolean authenticated = false;

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        authenticated = b;
    }

    public String getToken(){
        return token;
    }

    public TokenParser.TYPE getTokenType(){
        return tokenParser.getType(token);
    }

    public TokenParser.ACCOUNT getTokenAccount(){
        return tokenParser.getAccountType(token);
    }

    public String getPassword(){
        return tokenParser.getPassword(token);
    }

    public String getUUID(){
        return tokenParser.getUUID(token);
    }

    public boolean isExpired(){
        return tokenParser.isExpired(token);
    }



    public Token_Authentication(String token, Set<SimpleGrantedAuthority> authorities, boolean authenticated) {
        this.token = token;
        this.authorities = authorities;
        this.authenticated = authenticated;
        if(authorities == null){
            throw new NullPointerException();
        }
        tokenParser = Application.tokenParser;
    }

    public Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(){
        return authorities;
    }

    public void addManagerAuthority(){
        authorities.add(new SimpleGrantedAuthority(TokenParser.ACCOUNT.ACCOUNT_MANAGER.name().toUpperCase()));
    }
    public void addHostAuthority(){
        authorities.add(new SimpleGrantedAuthority(TokenParser.ACCOUNT.ACCOUNT_HOST.name().toUpperCase()));
    }
    public void addParticipatorAuthority(){
        authorities.add(new SimpleGrantedAuthority(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name().toUpperCase()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @Deprecated
    public String getName() {
        return getUUID();
    }

    @Override //Information for AuthenticationProvider
    @Deprecated
    public Object getCredentials() {
        return getPassword();
    }

    @Override
    @Deprecated //Could store additional info here
    public Object getDetails() {
        return getToken();
    }

    @Override
    @Deprecated //Information that describes user  Could be username / additional class
    public Object getPrincipal() {
        return getUUID();
    }

    @Deprecated
    public Token_Authentication() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token_Authentication that = (Token_Authentication) o;

        if (authenticated != that.authenticated) return false;
        if (!token.equals(that.token)) return false;
        return authorities.equals(that.authorities);

    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + authorities.hashCode();
        result = 31 * result + (authenticated ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Token_Authentication{" +
                "token='" + token + '\'' +
                ", authorities=" + authorities +
                ", authenticated=" + authenticated +
                '}';
    }
}
