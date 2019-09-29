package com.I_am_here.Security;

import com.I_am_here.Application;
import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
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
import java.util.HashSet;
import java.util.regex.Pattern;

@Component
public class TokenFilter extends OncePerRequestFilter {

    public static final String WEB_PATH = "/web/*";
    public static final String APP_PATH = "/app/*";

    private static Pattern web_pattern;
    private static Pattern host_pattern;
    private static Pattern participator_pattern;
    static {
        web_pattern = Pattern.compile("/web/[a-zA-Z_]*");
        host_pattern = Pattern.compile("/app/host/[a-zA-Z_]*");
        participator_pattern = Pattern.compile("/app/participator/[a-zA-Z_]*");
    }

    private boolean isPathCorrect(String token, String path){
        TokenParser tokenParser = Application.tokenParser;
        TokenParser.ACCOUNT type = tokenParser.getAccountType(token);
        System.out.println("Checking path " + path + " for type " + type);
        if(type == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
            return web_pattern.matcher(path).matches();
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            return host_pattern.matcher(path).matches();
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
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

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String access_token = httpServletRequest.getHeader("access_token");

        String path =  httpServletRequest.getRequestURI();

        boolean stop = false;
        System.out.println("Filtering path: " + path);
        System.out.println("Access token: " + access_token);
        switch (path) {
            case "/web/login": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGIN,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                stop = true;
                return;
            }
            case "/web/logout": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.LOGOUT,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                stop = true;
                return;
            }
            case "/web/register": {
                AnonymousAuthentication authentication = new AnonymousAuthentication(AnonymousAuthentication.AuthType.REGISTER,
                        TokenParser.ACCOUNT.ACCOUNT_MANAGER, true);
                saveAuth(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                stop = true;
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
                Access_token_Authentication authentication;
                TokenParser tokenParser = Application.tokenParser;
                try{
                    if(access_token == null){
                        System.err.println("Something whent wrong with access token");
                    }
                    if(
                            tokenParser.isExpired(
                                    access_token)){
                        authentication = new Access_token_Authentication(access_token, empAuthList(), false);
                        saveAuth(authentication);
                        System.out.println("Expired date");
                        break;
                    }
                    if(!isPathCorrect(access_token, path)){
                        authentication = new Access_token_Authentication(access_token, empAuthList(), false);
                        System.out.println("Path not correct");
                        saveAuth(authentication);
                        break;
                    }
                    authentication = new Access_token_Authentication(access_token, empAuthList(), true);
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
                }catch (Exception e){
                    e.printStackTrace();
                    SecurityContextHolder.clearContext();
                }

            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void saveAuth(Authentication authentication){
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private HashSet<SimpleGrantedAuthority> empAuthList(){
        return new HashSet<SimpleGrantedAuthority>();
    }


}
