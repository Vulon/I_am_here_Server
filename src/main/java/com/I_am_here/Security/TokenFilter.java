package com.I_am_here.Security;

import com.I_am_here.Application;
import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.regex.Pattern;

import static com.I_am_here.Application.tokenParser;

/**
 * Filter - used in Spring Security. Spring creates token chain, where executes filters.
 * Filter can get access to all data, stored in a http request, cookies, session.
 * If caller tries to access /web/login, /web/register, /web/login, /app/login, /app/register it creates AnonymousAuthentication,
 * If user tries to access /web/* path he needs valid token for type ACCOUNT_MANAGER
 * /app/host requires ACCOUNT_HOST
 * /app/participator requires ACCOUNT_PARTICIPATOR
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    public static final String WEB_PATH = "/web/*";
    public static final String APP_PATH = "/app/*";

    private static Pattern web_pattern;
    private static Pattern host_pattern;
    private static Pattern participator_pattern;
    private static Pattern refresh_pattern;
    private static Pattern app_pattern;
    static {
        web_pattern = Pattern.compile("/web/[a-zA-Z_]*");
        host_pattern = Pattern.compile("/app/host/[a-zA-Z_]*");
        participator_pattern = Pattern.compile("/app/participator/[a-zA-Z_]*");
        refresh_pattern = Pattern.compile("/[a-zA-Z_]*/refresh");
        app_pattern = Pattern.compile("/app/[a-zA-Z_]*");
    }



    /**
     * This method invokes, when filter starts filtering the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {




        String path =  httpServletRequest.getRequestURI();
        try{
            if(!filterUnsecuredWebPath(path)){
                if(!filterUnsecuredAppPath(path, httpServletRequest)){
                    if(!filterRefreshPath(path, httpServletRequest)){
                        String access_token = httpServletRequest.getHeader("access_token");
                        if(!filterAccessWebPath(path, access_token)){
                            if(!filterAccessAppPath(path, access_token)){
                                clearAuth();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            clearAuth();
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Saves authentication to context, Authentication.isAuthenticated() determines if this user should be trusted
     * @param authentication (Token_Authentication of AnonymousAuthentication object, that stores current user credentials)
     */
    private void saveAuth(Authentication authentication){
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //System.out.println("Filter finished at " + Date.from(Instant.now()) + "  " + Date.from(Instant.now()).getTime());
    }

    private void clearAuth(){
        SecurityContextHolder.clearContext();
    }

    private boolean filterUnsecuredWebPath(String path){

        switch (path){
            case "/web/login":{
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGIN,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                return true;
            }
            case "/web/register":{
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.REGISTER,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                return true;
            }
            case "/web/logout":{
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGOUT,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private boolean filterUnsecuredAppPath(String path, HttpServletRequest httpServletRequest) {
        String type_header = httpServletRequest.getHeader("account_type");
        if(type_header == null) {
            return false;
        }
        TokenParser.ACCOUNT account_type;
        try {
            account_type = TokenParser.ACCOUNT.valueOf(type_header);
        }catch (Exception e) {
            return false;
        }
        if(!(account_type == TokenParser.ACCOUNT.ACCOUNT_HOST) && !(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR)){
            return false;
        }

        switch (path) {
            case "/app/login":{
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGIN,
                        account_type, true);
                saveAuth(authentication);
                return true;
            }
            case "/app/register":{
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.REGISTER,
                        account_type, true);
                saveAuth(authentication);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private boolean filterRefreshPath(String path, HttpServletRequest httpServletRequest) {
        String refresh_token = httpServletRequest.getHeader("refresh_token");
        if(refresh_token == null) {
            return false;
        }

        if(refresh_pattern.matcher(path).matches()) {
            if(tokenParser.isExpired(refresh_token)){
                saveAuth(new Token_Authentication(refresh_token, empAuthList(), false));
                System.out.println("Expired date");
                return false;
            }
            Token_Authentication authentication = new Token_Authentication(refresh_token, empAuthList(), true);
            if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
                authentication.addManagerAuthority();
            }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_HOST){
                authentication.addHostAuthority();
            }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                authentication.addParticipatorAuthority();
            }
            System.out.println(authentication);
            saveAuth(authentication);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean filterAccessWebPath(String path, String access_token) {
        if(!web_pattern.matcher(path).matches()) {
            return false;
        }
        if(access_token == null){
            return false;
        }
        if(tokenParser.isExpired(access_token)) {
            return false;
        }
        TokenParser.ACCOUNT account_type = tokenParser.getAccountType(access_token);
        if(account_type != TokenParser.ACCOUNT.ACCOUNT_MANAGER) {
            return false;
        }
        Token_Authentication authentication = new Token_Authentication(access_token, empAuthList(), true);
        authentication.addManagerAuthority();
        saveAuth(authentication);
        return true;
    }

    private boolean filterAccessAppPath(String path, String access_token){
        if(!app_pattern.matcher(path).matches()
        && !host_pattern.matcher(path).matches()
        && !participator_pattern.matcher(path).matches()) {
            return false;
        }


        if(access_token == null) {
            return false;
        }
        if(tokenParser.isExpired(access_token)) {
            return false;
        }
        TokenParser.ACCOUNT account_type = tokenParser.getAccountType(access_token);
        if(account_type != TokenParser.ACCOUNT.ACCOUNT_HOST && account_type != TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR) {
            return false;
        }
        Token_Authentication authentication = new Token_Authentication(access_token, empAuthList(), true);
        if(account_type == TokenParser.ACCOUNT.ACCOUNT_HOST) {
            authentication.addHostAuthority();
        }else{
            authentication.addParticipatorAuthority();
        }
        saveAuth(authentication);
        return true;
    }


    /**
     * Used to conveniently generate empty HashSet of class SimpleGrantedAuthority
     * @return empty HashSet
     */
    private HashSet<SimpleGrantedAuthority> empAuthList(){
        return new HashSet<SimpleGrantedAuthority>();
    }


}
