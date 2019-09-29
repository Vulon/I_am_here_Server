package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;


@RestController
public class AndroidRestController {

    private HostRepository hostRepository;
    private ParticipatorRepository participatorRepository;
    private ManagerRepository managerRepository;
    private TokenParser tokenParser;

    public AndroidRestController(HostRepository hostRepository, ParticipatorRepository participatorRepository, ManagerRepository managerRepository, TokenParser tokenParser) {
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
    }

    @PostMapping("/app/register")
    public ResponseEntity<TokenData> register(
            @RequestParam String UUID,
            @RequestParam String password,
            @RequestHeader(name = "account_type") String account_type,
            @RequestParam(name = "name", defaultValue = "name", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email,
            @RequestParam(name = "phone_number", defaultValue = "", required = false) String phone_number){

        TokenParser.ACCOUNT type = TokenParser.ACCOUNT.valueOf(account_type);
        Account account = getAccount(UUID, password, type);
        if(account != null){
            return new ResponseEntity<>(new TokenData(), HttpStatus.CONFLICT);
        }
        Date now = Date.from(Instant.now());

        if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, now, TokenParser.ACCOUNT.ACCOUNT_HOST);
            String refresh_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.REFRESH, now, TokenParser.ACCOUNT.ACCOUNT_HOST);

            Host host = new Host(UUID, name, email, phone_number, password, access_token, refresh_token, new HashSet<>()
            , null, new HashSet<>());
            hostRepository.saveAndFlush(host);
            TokenData tokenData = new TokenData(access_token, refresh_token, tokenParser.getExpitaionDate(access_token),
                    tokenParser.getExpitaionDate(refresh_token));
            return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, now, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR);
            String refresh_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.REFRESH, now, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR);

            Participator participator = new Participator(UUID, name, email, phone_number, password, new HashSet<>(),
                    access_token, refresh_token, new HashSet<>(), new HashSet<>());
            participatorRepository.saveAndFlush(participator);

            TokenData tokenData = new TokenData(access_token, refresh_token, tokenParser.getExpitaionDate(access_token),
                    tokenParser.getExpitaionDate(refresh_token));
            return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
        }else{
            return new ResponseEntity<TokenData>(new TokenData(), HttpStatus.BAD_REQUEST);
        }
    }

    private Account getAccount(String UUID, String password, TokenParser.ACCOUNT type){
        if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
            return participator;
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            Host host = hostRepository.findByUuidAndPassword(UUID,password);
            return host;
        }else{
            return null;
        }
    }

}
