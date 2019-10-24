package com.I_am_here.Services;


import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Subject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class QRParser {
    /*
    subject id  - Integer id
    host_id - Host UUID
    issue date
    * */
    SecretDataLoader secretDataLoader;

    public QRParser(SecretDataLoader secretDataLoader) {
        this.secretDataLoader = secretDataLoader;
    }

    public String createQrToken(Subject subject, Host host, Date now){
        return Jwts.builder()
                .setSubject(subject.getSubject_id().toString())
                .setIssuer(host.getUuid())
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, getEncodedQRTokenKey())
                .compact();
    }

    public Integer getSubjectID(String qr_token){
        return Integer.parseInt(getClaims(qr_token).getSubject());
    }

    public String getHostUUID(String qr_token){
        return getClaims(qr_token).getIssuer();
    }

    public Date getDate(String qr_token){
        return getClaims(qr_token).getIssuedAt();
    }

    private Claims getClaims(String token){
        return Jwts.parser().setSigningKey(getEncodedQRTokenKey())
                .parseClaimsJws(token).getBody();
    }

    private byte[] getEncodedQRTokenKey(){
        return TextCodec.BASE64.decode(secretDataLoader.getSecretQrTokenKey());
    }
}
