package com.I_am_here.Security;

import com.I_am_here.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;



public class Access_token_AuthenticationToken implements Authentication {
    //Stores access token during session.


    private TokenParser tokenParser;



    private String access_token;

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

    public String getAccess_token(){
        return access_token;
    }

    public String getPassword(){
        return tokenParser.getPassword(access_token);
    }

    public String getUUID(){
        return tokenParser.getUUID(access_token);
    }

    public boolean isExpired(){
        return tokenParser.isExpired(access_token);
    }



    public Access_token_AuthenticationToken(String access_token, Set<SimpleGrantedAuthority> authorities, boolean authenticated) {
        this.access_token = access_token;
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
        return getAccess_token();
    }

    @Override
    @Deprecated //Information that describes user  Could be username / additional class
    public Object getPrincipal() {
        return getUUID();
    }

    @Deprecated
    public Access_token_AuthenticationToken() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Access_token_AuthenticationToken that = (Access_token_AuthenticationToken) o;

        if (authenticated != that.authenticated) return false;
        if (!access_token.equals(that.access_token)) return false;
        return authorities.equals(that.authorities);

    }

    @Override
    public int hashCode() {
        int result = access_token.hashCode();
        result = 31 * result + authorities.hashCode();
        result = 31 * result + (authenticated ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Access_token_AuthenticationToken{" +
                "access_token='" + access_token + '\'' +
                ", authorities=" + authorities +
                ", authenticated=" + authenticated +
                '}';
    }
}
