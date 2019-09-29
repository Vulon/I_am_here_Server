package com.I_am_here.Security;

import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Repository.ManagerRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.I_am_here.Configuration.SecurityConfig.MANAGER_ROLE;


@Service
public class Token_AuthenticationProvider implements AuthenticationProvider {

    private ManagerRepository managerRepository;



    public Token_AuthenticationProvider(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Access_token_AuthenticationToken tokenAuthentication = (Access_token_AuthenticationToken)authentication;
        boolean isExpired = tokenAuthentication.isExpired();
        String UUID = tokenAuthentication.getUUID();
        String password = tokenAuthentication.getPassword();
        String access_token = tokenAuthentication.getAccess_token();

        if(access_token == null || access_token.length() < 5){
            throw new BadCredentialsException("Access token not found");
        }
        if(UUID.length() < 5 || password.length() < 5){
            throw new BadCredentialsException("Password or UUID is to short");
        }
        if(isExpired){
            throw new BadCredentialsException("Access token expired");
        }
        Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
        if(manager == null){
            throw new BadCredentialsException("Account with that UUID and password was not found");
        }
        if(!manager.getAccess_token().equals(access_token)){
            throw new BadCredentialsException("Access token mismatch");
        }
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(MANAGER_ROLE));
        return new Access_token_AuthenticationToken(access_token, tokenAuthentication.getSimpleGrantedAuthorities(), true);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Access_token_AuthenticationToken.class);
    }
}
