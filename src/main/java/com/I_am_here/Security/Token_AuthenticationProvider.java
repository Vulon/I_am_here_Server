package com.I_am_here.Security;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Participator;
import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.Instant;


/**
 * This class is used for Security. It invokes 'authenticate()', when user tries to access protected url.
 */
@Service
public class Token_AuthenticationProvider implements AuthenticationProvider {

    private ManagerRepository managerRepository;
    private HostRepository hostRepository;
    private ParticipatorRepository participatorRepository;
    private TokenParser tokenParser;


    public Token_AuthenticationProvider(ManagerRepository managerRepository, HostRepository hostRepository, ParticipatorRepository participatorRepository, TokenParser tokenParser) {
        this.managerRepository = managerRepository;
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.tokenParser = tokenParser;
    }

    /**
     * This method takes authentication instance. It loads account from database and checks if
     * token from database is equal to the one in authentication.
     * @param authentication AnonymousAuthentication or Token_Authentication instance
     * @return verified Authentication
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("Auth Provider started at " + Date.from(Instant.now()) + "  " + Date.from(Instant.now()).getTime());
        try{
            Token_Authentication auth = (Token_Authentication)authentication;
            String token = auth.getToken();
            Account account = findAccount(token);
            if (account == null){
                auth.setAuthenticated(false);
                return finish(auth);
            }
            if(!auth.getPassword().equals(account.getPassword())){
                auth.setAuthenticated(false);
                return finish(auth);
            }
            if(auth.getTokenType() == TokenParser.TYPE.ACCESS && !account.getAccessToken().equals(auth.getToken())){
                auth.setAuthenticated(false);
                return finish(auth);
            }
            if(auth.getTokenType() == TokenParser.TYPE.REFRESH && !account.getRefreshToken().equals(auth.getToken())){
                auth.setAuthenticated(false);
                return finish(auth);
            }
            auth.setAuthenticated(true);
            return finish(auth);

        }catch (Exception e){
            System.out.println("Cannot convert authentication to token auth");
        }
        try{
            AnonymousAuthentication auth = (AnonymousAuthentication)authentication;
            return finish(auth);
        }catch (Exception e){
            System.out.println("Could not convert this authentication: " + authentication.toString());
            authentication.setAuthenticated(false);
            return finish(authentication);
        }
    }

    /**
     * This method is used only for debug purpose to track time needed for processing request
     * @param auth Authentication
     * @return the same auth object
     */
    private Authentication finish(Authentication auth){
        //System.out.println("Auth provider finished at " + Date.from(Instant.now()) + "  " + Date.from(Instant.now()).getTime());
        return auth;
    }



    private Account findAccount(String token){
        TokenParser.ACCOUNT type = tokenParser.getAccountType(token);
        String password = tokenParser.getPassword(token);
        String UUID = tokenParser.getUUID(token);

        if(type == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
            Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
            return manager;

        }else if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            Host host  = hostRepository.findByUuidAndPassword(UUID, password);
            return host;
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
            return participator;
        }else{
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Token_Authentication.class);
    }
}
