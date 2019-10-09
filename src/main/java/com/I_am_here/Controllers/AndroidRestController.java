package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.*;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.SecretDataLoader;
import com.I_am_here.TransportableData.PartyData;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * ServerController. Methods of this class get called when server gets requests for urls like /app/*
 * Most of this methods can be called only if a request has a valid access token.
 * This controller is designed to work with Host and Participator accounts.
 */
@RestController
public class AndroidRestController {

    private HostRepository hostRepository;
    private ParticipatorRepository participatorRepository;
    private PartyRepository partyRepository;
    private SubjectRepository subjectRepository;

    private ManagerRepository managerRepository;
    private TokenParser tokenParser;
    private SecretDataLoader secretDataLoader;


    public AndroidRestController(HostRepository hostRepository, ParticipatorRepository participatorRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, ManagerRepository managerRepository, TokenParser tokenParser, SecretDataLoader secretDataLoader) {
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
        this.secretDataLoader = secretDataLoader;
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
            Host host = new Host(UUID, name, email, phone_number, password, data);
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
            Participator participator = new Participator(UUID, name, email, phone_number, password, data);
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

    @GetMapping("/app/participator/find_party")
    public ResponseEntity<Set<PartyData>> getPartyList(
            @RequestHeader String access_token,
            @RequestParam String code_word
    ){
        Date broadcast_start = Date.from(Instant.now().minusSeconds(secretDataLoader.getPartyBroadcastDuration()));
        Set<Party> partyList = partyRepository.getAllByBroadcastWord(code_word);
        if(partyList == null){
            return new ResponseEntity<>(new HashSet<PartyData>(), HttpStatus.OK);
        }
        System.out.println("BroadCast start: " + broadcast_start);
        return  new ResponseEntity<>(PartyData.createPartyData(partyList), HttpStatus.OK);
    }

    @PostMapping("/app/participator/join_party")
    public ResponseEntity<String> joinParty(
            @RequestParam Integer party_id,
            @RequestParam String code_word,
            @RequestHeader String access_token
    ){
        Party party = partyRepository.findByParty(party_id);
        if(party == null){
            return new ResponseEntity<>("Not found", HttpStatus.CONFLICT);
        }
        if(party.getBroadcastWord().equals(code_word)){
            Participator participator = (Participator)getAccount(access_token);
            System.out.println("Found party: " + party);
            System.out.println("Found Participator: " + participator);

            party.addParticipator(participator);
            party = partyRepository.saveAndFlush(party);
            System.out.println("Result party: " + party);
            participator.addParty(party);
            System.out.println("SEMI Result participator: " + participator);
            participator = participatorRepository.saveAndFlush(participator);
            System.out.println("Result participator: " + participator);


            return new ResponseEntity<>("Joined repository " + party.getName(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Code word mismatch", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/app/participator/my_party_list")
    public ResponseEntity<Set<PartyData>> getPartiesByParticipator(
            @RequestHeader String access_token
    ){
        Participator participator = (Participator)getAccount(access_token);
        return new ResponseEntity<>(PartyData.createPartyData(participator.getParties()), HttpStatus.OK);
    }

    private Account getAccount(String access_token){
        try{
            TokenParser.ACCOUNT account = tokenParser.getAccountType(access_token);
            String UUID = tokenParser.getUUID(access_token);
            String password = tokenParser.getPassword(access_token);
            if(account == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
                return managerRepository.findByUuidAndPassword(UUID, password);
            }else if(account == TokenParser.ACCOUNT.ACCOUNT_HOST){
                return hostRepository.findByUuidAndPassword(UUID, password);
            }else if(account == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                return participatorRepository.findByUuidAndPassword(UUID, password);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
