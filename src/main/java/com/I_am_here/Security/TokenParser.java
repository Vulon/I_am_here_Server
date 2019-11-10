package com.I_am_here.Security;


import com.I_am_here.Database.Account;
import com.I_am_here.Services.SecretDataLoader;
import com.I_am_here.TransportableData.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * TokenParser is used to work with tokens. It can create new tokens, get data from tokens, create new TokenData instances.
 * This Service uses jsonwebtoken library
 * Service - should be autowired, or included in constructor. Needs to be initialized by Spring.
 */
@Service
public class TokenParser {


    private SecretDataLoader secretDataLoader;



    public TokenParser(SecretDataLoader secretDataLoader, Environment env) {
        this.secretDataLoader = secretDataLoader;
    }

    /**
    This enum defines, what for what role a token was granted. Also defines which database to use, when processing
    http request
     */
    public static enum ACCOUNT{
        ACCOUNT_MANAGER,
        ACCOUNT_HOST,
        ACCOUNT_PARTICIPATOR
    }
    /**
    Type of token. Access token is used to authorize on server and get access to server functions, this token has short life.
    Refresh token is used to get new access token. Refresh token expires in about a month
     */
    public static enum TYPE{
        ACCESS,
        REFRESH
    }

    /**
     * Creates new token based on entered data
     * @param UUID UUID that caller gets from FireBase
     * @param password password, that user invents
     * @param type type of token (TYPE.ACCESS, TYPE.REFRESH)
     * @param date date when a token is created
     * @param account_type ACCOUNT enum member (ACCOUNT_HOST, ACCOUNT_MANAGER, ACCOUNT_PARTICIPATOR)
     * @return String - token
     */
    public String createToken(String UUID, String password, TYPE type, Date date, ACCOUNT account_type){
        Date start = calculateExpirationDate(date, type);
        byte[] bytes = getEncodedSecretKey();
        JwtBuilder builder = Jwts.builder();
        builder.setSubject(UUID);
        builder.setIssuedAt(date);
        builder.setExpiration(start);
        builder.claim("t", type);
        builder.claim("p", password);
        builder.claim("a", account_type);
        builder.signWith(SignatureAlgorithm.HS256, bytes);
        String jwt = builder.compact();
        return jwt;
    }

    /**
     * Parses the token and gets mapped data, stored in this token
     * @param token Access or Refresh token
     * @return Map of data, stored in that token
     */
    private Claims getClaims(String token){
        return Jwts.parser().setSigningKey(getEncodedSecretKey())
                .parseClaimsJws(token).getBody();
    }

    /**
     * Created TokenData instance based on parameters. Access and Refresh tokens are generated here.
     * @param UUID UUID that caller gets from FireBase
     * @param password Password that user created
     * @param accountType ACCOUNT enum member (ACCOUNT_HOST, ACCOUNT_MANAGER, ACCOUNT_PARTICIPATOR)
     * @param now Time when the token is created
     * @return TokenData instance, containing new generated tokens
     */
    public TokenData createTokenData(String UUID, String password, ACCOUNT accountType, Date now){
        String access = createToken(UUID, password, TYPE.ACCESS, now, accountType);
        String refresh = createToken(UUID, password, TYPE.REFRESH, now, accountType);
        TokenData tokenData = new TokenData(access, refresh, getExpirationDate(access), getExpirationDate(refresh));
        return tokenData;
    }

    /**
     * Creates TokenData instance from data, contained in Account interface implementation.
     * Tokens match ones stored in account
     * @param account Account Interface implementation
     *                (Manager, Host, Participator)
     * @return TokenData instance
     */

    public TokenData getTokenData(Account account){
        String access = account.getAccessToken();
        String refresh = account.getRefreshToken();
        TokenData tokenData = new TokenData(access, refresh, getExpirationDate(access), getExpirationDate(refresh));
        return tokenData;
    }


    /**
     * Get password, stashed in the token
     * @param token Access or Refresh token
     * @return Password
     */
    public String getPassword(String token){

        return getClaims(token).get("p", String.class);
    }

    public String getUUID(String token){
        return getClaims(token).getSubject();
    }

    public Date getIssuedDate(String token){
        return getClaims(token).getIssuedAt();
    }

    public Date getExpirationDate(String token){
        return getClaims(token).getExpiration();
    }

    public TYPE getType(String token){
        String s = getClaims(token).get("t", String.class);
        return TYPE.valueOf(s);
    }

    public ACCOUNT getAccountType(String token){
        String s =  getClaims(token).get("a", String.class);
        return ACCOUNT.valueOf(s);
    }

    public boolean isExpired(String token){
        Claims claims = getClaims(token);
        Date expireDate = claims.getExpiration();
        Date now = Date.from(Instant.now());
        return now.after(expireDate);
    }

    private Date calculateExpirationDate(Date now, TYPE tokenType){
        long seconds;
        if(tokenType == TYPE.ACCESS){
            seconds = secretDataLoader.getAccessTokenValidity();
        }else if(tokenType == TYPE.REFRESH){
            seconds = secretDataLoader.getRefreshTokenValidity();
        }else{
            throw new RuntimeException("Not supported token type: " + tokenType);
        }
        return Date.from(Instant.ofEpochMilli(now.getTime()).plusSeconds(seconds));
    }

    private byte[] getEncodedSecretKey(){
        return TextCodec.BASE64.decode(secretDataLoader.getSecretTokenKey());
    }
}
