package com.I_am_here.Security;


import com.I_am_here.Services.SecretDataLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenParser {



    private SecretDataLoader secretDataLoader;
    private Environment env;

    public TokenParser(SecretDataLoader secretDataLoader, Environment env) {
        this.secretDataLoader = secretDataLoader;
        this.env = env;
    }

    public static enum ACCOUNT{
        ACCOUNT_MANAGER,
        ACCOUNT_HOST,
        ACCOUNT_PARTICIPATOR
    }
    public static enum TYPE{
        ACCESS,
        REFRESH
    }


    public String CreateToken(String UUID, String password, TYPE type, Date date, ACCOUNT account_type){
        String jwt = Jwts.builder()
                .setSubject(UUID)
                .setIssuedAt(date)
                .setExpiration(calculateExpirationDate(date, type))
                .claim("t", type)
                .claim("p", password)
                .claim("a", account_type)
                .signWith(SignatureAlgorithm.HS256, getEncodedSecretKey())
                .compact();
        return jwt;
    }

    private Claims getClaims(String token){
        return Jwts.parser().setSigningKey(getEncodedSecretKey())
                .parseClaimsJws(token).getBody();
    }



    public String getPassword(String token){
        return getClaims(token).get("p", String.class);
    }

    public String getUUID(String token){
        return getClaims(token).getSubject();
    }

    public Date getIssuedDate(String token){
        return getClaims(token).getIssuedAt();
    }

    public Date getExpitaionDate(String token){
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
            seconds = Long.parseLong(env.getProperty("access_token_life_seconds"));
        }else if(tokenType == TYPE.REFRESH){
            seconds = Long.parseLong(env.getProperty("refresh_token_life_seconds"));
        }else{
            throw new RuntimeException("Not supported token type: " + tokenType);
        }
        return Date.from(Instant.ofEpochSecond(now.getTime()).plusSeconds(seconds));
    }

    private byte[] getEncodedSecretKey(){
        return TextCodec.BASE64.decode(secretDataLoader.getSecretTokenKey());
    }
}
