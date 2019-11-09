package com.I_am_here.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

public class AnonymousAuthentication implements Authentication {

    public static enum AuthType{
        REGISTER,
        LOGIN,
        LOGOUT
    }

    private AuthType authType;

    private TokenParser.ACCOUNT accountType;

    private boolean authenticated;


    public AnonymousAuthentication(AuthType authType, TokenParser.ACCOUNT accountType, boolean authenticated) {
        this.authType = authType;
        this.accountType = accountType;
        this.authenticated = authenticated;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public TokenParser.ACCOUNT getAccountType() {
        return accountType;
    }

    public void setAccountType(TokenParser.ACCOUNT accountType) {
        this.accountType = accountType;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        authenticated = b;
    }

    @Override
    @Deprecated
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<SimpleGrantedAuthority>();
    }

    @Override
    @Deprecated
    public Object getCredentials() {
        return getAccountType();

    }

    @Override
    @Deprecated
    public Object getDetails() {
        return getAuthType();
    }

    @Override
    @Deprecated
    public Object getPrincipal() {
        return getAccountType();
    }

    @Override
    public String toString() {
        return "AnonymousAuthentication{" +
                "authType=" + authType +
                ", accountType=" + accountType +
                ", authenticated=" + authenticated +
                '}';
    }

    @Override
    @Deprecated
    public String getName() {
        return getAccountType().name();
    }
}
