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


    public String CreateToken(String UUID, String password, String type, Date date){
        String jwt = Jwts.builder().setIssuer("i_am_here_server")
                .setSubject(UUID)
                .setIssuedAt(date)
                .setExpiration(calculateExpirationDate(date, type))
                .claim("type", type)
                .claim("password", password)
                .signWith(SignatureAlgorithm.HS256, getEncodedSecretKey())
                .compact();
        return jwt;
    }

    private Claims getClaims(String token){
        return Jwts.parser().setSigningKey(getEncodedSecretKey())
                .parseClaimsJws(token).getBody();
    }



    public String getPassword(String token){
        return getClaims(token).get("password", String.class);
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

    public String getType(String token){
        return getClaims(token).get("type", String.class);
    }

    public boolean isExpired(String token){
        Claims claims = getClaims(token);
        Date expireDate = claims.getExpiration();
        Date now = Date.from(Instant.now());
        return now.after(expireDate);
    }

    private Date calculateExpirationDate(Date now, String tokenType){
        long seconds;
        if(tokenType.equals("access")){
            seconds = Long.parseLong(env.getProperty("access_token_life_seconds"));
        }else if(tokenType.equals("refresh")){
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
