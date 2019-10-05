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
    static {
        web_pattern = Pattern.compile("/web/[a-zA-Z_]*");
        host_pattern = Pattern.compile("/app/host/[a-zA-Z_]*");
        participator_pattern = Pattern.compile("/app/participator/[a-zA-Z_]*");
        refresh_pattern = Pattern.compile("/[a-zA-Z_]*/refresh");
    }

    /**
     * Checks if the entered token can be used to access this url path
     * @param token Token used in authentication
     * @param path Url path that caller tries to access
     * @return true - if caller can access this url with that token, false otherwise
     */
    private boolean isPathCorrect(String token, String path){
        TokenParser tokenParser = Application.tokenParser;
        TokenParser.ACCOUNT account_type = tokenParser.getAccountType(token);
        TokenParser.TYPE token_type = tokenParser.getType(token);
        System.out.println("Checking path " + path + " for type " + account_type);
        if(token_type == TokenParser.TYPE.REFRESH){
            return refresh_pattern.matcher(path).matches();
        }
        if(account_type == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
            return web_pattern.matcher(path).matches();
        }else if(account_type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            return host_pattern.matcher(path).matches();
        }else if(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            return participator_pattern.matcher(path).matches();
        }else{
            return false;
        }
    }


    public static final String LOGIN_TOKEN = "login";
    public static final String REGISTER_TOKEN = "register";



    @Autowired
    private HostRepository hostRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private ParticipatorRepository participatorRepository;

    /**
     * This method invokes, when filter starts filtering the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String access_token = httpServletRequest.getHeader("access_token");

        String refresh_token = httpServletRequest.getHeader("refresh_type");

        String path =  httpServletRequest.getRequestURI();
        //System.out.println("Filter start at: " + Date.from(Instant.now()) + "  " + Date.from(Instant.now()).getTime());
        switch (path) {
            case "/web/login": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGIN,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            case "/web/logout": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGOUT,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            case "/web/register": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.REGISTER,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
        }
        String type_header = httpServletRequest.getHeader("account_type");
        if(type_header == null){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        TokenParser.ACCOUNT account_type = TokenParser.ACCOUNT.valueOf(httpServletRequest.getHeader("account_type"));
        switch (path){
            case "/app/login":{
                if(!(account_type == TokenParser.ACCOUNT.ACCOUNT_HOST) && !(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR)){
                    break;
                }
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGIN,
                        account_type, true);
                saveAuth(authentication);
                break;
            }
            case "/app/register":{
                if(!(account_type == TokenParser.ACCOUNT.ACCOUNT_HOST) && !(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR)){
                    break;
                }
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.REGISTER,
                        account_type, true);
                saveAuth(authentication);
                break;
            }
            default:{
                System.out.println("ENTERED Default case");
                Token_Authentication authentication;
                TokenParser tokenParser = Application.tokenParser;
                try{
                    if(access_token != null){
                        if(tokenParser.isExpired(access_token)){
                            authentication = new Token_Authentication(access_token, empAuthList(), false);
                            saveAuth(authentication);
                            System.out.println("Expired date");
                            break;
                        }
                        if(!isPathCorrect(access_token, path)){
                            authentication = new Token_Authentication(access_token, empAuthList(), false);
                            System.out.println("Path not correct");
                            saveAuth(authentication);
                            break;
                        }
                        authentication = new Token_Authentication(access_token, empAuthList(), true);
                        System.out.println("Created Authentication");
                        if(tokenParser.getAccountType(access_token) == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
                            authentication.addManagerAuthority();
                        }else if(tokenParser.getAccountType(access_token) == TokenParser.ACCOUNT.ACCOUNT_HOST){
                            authentication.addHostAuthority();
                        }else if(tokenParser.getAccountType(access_token) == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                            authentication.addParticipatorAuthority();
                        }
                        System.out.println(authentication);
                        saveAuth(authentication);

                    }else if(refresh_token != null){
                        if(tokenParser.isExpired(refresh_token)){
                            authentication = new Token_Authentication(refresh_token, empAuthList(), false);
                            saveAuth(authentication);
                            System.out.println("Expired date");
                            break;
                        }
                        if(!isPathCorrect(refresh_token, path)){
                            authentication = new Token_Authentication(refresh_token, empAuthList(), false);
                            System.out.println("Path not correct");
                            saveAuth(authentication);
                            break;
                        }
                        authentication = new Token_Authentication(refresh_token, empAuthList(), true);
                        System.out.println("Created Authentication");
                        if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
                            authentication.addManagerAuthority();
                        }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_HOST){
                            authentication.addHostAuthority();
                        }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                            authentication.addParticipatorAuthority();
                        }
                        System.out.println(authentication);
                        saveAuth(authentication);
                    }else{
                        authentication = new Token_Authentication(refresh_token, empAuthList(), false);
                        saveAuth(authentication);
                        System.err.println("Something went wrong with access token");
                        break;
                    }



                }catch (Exception e){
                    e.printStackTrace();
                    SecurityContextHolder.clearContext();
                }

            }
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


    /**
     * Used to conveniently generate empty HashSet of class SimpleGrantedAuthority
     * @return empty HashSet
     */
    private HashSet<SimpleGrantedAuthority> empAuthList(){
        return new HashSet<SimpleGrantedAuthority>();
    }


}
