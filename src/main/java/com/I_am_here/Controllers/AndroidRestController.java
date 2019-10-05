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



/**
 * ServerController. Methods of this class get called when server gets requests for urls like /app/*
 * Most of this methods can be called only if a request has a valid access token.
 * This controller is designed to work with Host and Participator accounts.
 */
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

    /**
     * This method creates new entry in database and returns TokenData containing new tokens.
     * If such account already exists, server returns status code 409.
     * If account type is wrong or misspelled, server returns status code 400
     * @param UUID - UUID from FireBase
     * @param password - user password
     * @param account_type - account type (ACCOUNT_HOST, ACCOUNT_PARTICIPATOR)
     * @param name - name. Not necessary
     * @param email - email Not necessary
     * @param phone_number - phone number. Not necessary
     */
    @PostMapping("/app/register")
    public ResponseEntity<TokenData> register(
            @RequestParam String UUID,
            @RequestParam String password,
            @RequestParam(name = "account_type") String account_type,
            @RequestParam(name = "name", defaultValue = "name", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email,
            @RequestParam(name = "phone_number", defaultValue = "", required = false) String phone_number){
        TokenParser.ACCOUNT type = TokenParser.ACCOUNT.valueOf(account_type);
        Account account = getAccount(UUID, password, type);
        if(account != null){
            return error(HttpStatus.CONFLICT);
        }
        Date now = Date.from(Instant.now());

        if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
            Host host = new Host(UUID, name, email, phone_number, password, data.getAccess_token(), data.getRefresh_token(), new HashSet<>()
            , null, new HashSet<>());
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
            Participator participator = new Participator(UUID, name, email, phone_number, password, new HashSet<>(),
                    data.getAccess_token(), data.getRefresh_token(), new HashSet<>(), new HashSet<>());
            participatorRepository.saveAndFlush(participator);

            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }else{
            return error(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/app/login")
    public ResponseEntity<TokenData> login(
            @RequestParam String UUID,
            @RequestParam String password,
            @RequestParam String account_type
    ){
        Date now = Date.from(Instant.now());
        if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_HOST.name())){
            Host host = hostRepository.findByUuidAndPassword(UUID, password);
            if(host == null){
                error(HttpStatus.CONFLICT);
            }
            TokenData tokenData = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
            host.setAccess_token(tokenData.getAccess_token());
            host.setRefresh_token(tokenData.getRefresh_token());
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);

        }else if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name())){
            Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
            if(participator == null){
                return error(HttpStatus.CONFLICT);
            }
            TokenData tokenData = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
            participator.setAccess_token(tokenData.getAccess_token());
            participator.setRefresh_token(tokenData.getRefresh_token());
            participatorRepository.saveAndFlush(participator);
            return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
        }else{
            return error(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/app/update_credentials")
    public ResponseEntity<String> updateHostCredentials(
            @RequestHeader String access_token,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "email", required = false, defaultValue = "")String email,
            @RequestParam(name = "phone_number", required = false, defaultValue = "")String phone_number
    ){
        try{
            TokenParser.ACCOUNT account = tokenParser.getAccountType(access_token);
            if(tokenParser.getType(access_token) != TokenParser.TYPE.ACCESS){
                return new ResponseEntity<String>("TOKEN invalid", HttpStatus.NOT_ACCEPTABLE);
            }
            String UUID = tokenParser.getUUID(access_token);
            String password = tokenParser.getPassword(access_token);

            if(account == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);
                if( host == null){
                    return new ResponseEntity<String>("TOKEN invalid", HttpStatus.NOT_ACCEPTABLE);
                }
                if(name.length() > 1){
                    host.setName(name);
                }
                if(email.length() > 1){
                    host.setEmail(email);
                }
                if(phone_number.length() > 1){
                    host.setPhone_number(phone_number);
                }
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>("Updated", HttpStatus.OK);
            }else if(account == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return new ResponseEntity<String>("TOKEN invalid", HttpStatus.NOT_ACCEPTABLE);
                }
                if(name.length() > 1){
                    participator.setName(name);
                }
                if(email.length() > 1){
                    participator.setEmail(email);
                }
                if(phone_number.length() > 1){
                    participator.setPhone_number(phone_number);
                }
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<>("Updated", HttpStatus.OK);
            }else{
                return new ResponseEntity<String>("Wrong account type", HttpStatus.BAD_REQUEST);
            }


        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>("TOKEN invalid", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/app/refresh")
    public ResponseEntity<TokenData> updateAccessToken(
            @RequestHeader String refresh_token
    ){
        try{
            TokenParser.ACCOUNT account_type = tokenParser.getAccountType(refresh_token);
            TokenParser.TYPE token_type = tokenParser.getType(refresh_token);
            if(token_type != TokenParser.TYPE.REFRESH){
                return error(HttpStatus.BAD_REQUEST);
            }
            String UUID = tokenParser.getUUID(refresh_token);
            String password = tokenParser.getPassword(refresh_token);

            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, Date.from(Instant.now()), account_type);
            if (account_type == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);
                if(host == null){
                    return error(HttpStatus.NOT_ACCEPTABLE);
                }
                host.setAccess_token(access_token);
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(tokenParser.getTokenData(host), HttpStatus.OK);
            }else if(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return error(HttpStatus.NOT_ACCEPTABLE);
                }
                participator.setAccess_token(access_token);
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<>(tokenParser.getTokenData(participator), HttpStatus.OK);
            }else{
                return error(HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(HttpStatus.BAD_REQUEST);
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

    private ResponseEntity<TokenData> error(HttpStatus status){
        return new ResponseEntity<TokenData>(new TokenData(), status);
    }

}
