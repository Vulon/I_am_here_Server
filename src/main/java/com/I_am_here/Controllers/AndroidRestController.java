package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.*;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.SecretDataLoader;
import com.I_am_here.Services.StatusCodeCreator;
import com.I_am_here.TransportableData.PartyData;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Part;
import java.time.Instant;
import java.util.*;


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
    private StatusCodeCreator statusCodeCreator;


    public AndroidRestController(HostRepository hostRepository, ParticipatorRepository participatorRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, ManagerRepository managerRepository, TokenParser tokenParser, SecretDataLoader secretDataLoader, StatusCodeCreator statusCodeCreator) {
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
        this.secretDataLoader = secretDataLoader;
        this.statusCodeCreator = statusCodeCreator;
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
     * @param phone_number - phone number.
     */
    @PostMapping("/app/register")
    public ResponseEntity<TokenData> register(
            @RequestHeader String UUID,
            @RequestHeader String password,
            @RequestParam(name = "account_type") String account_type,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email,
            @RequestParam(name = "phone_number") String phone_number){
        try{
            System.out.println("Got phone number: " + phone_number);
            if(phone_number.length() == 11){
                phone_number = "+" + phone_number;
            }else if(phone_number.length() == 12){
                phone_number = "+" + phone_number.substring(1, 12);
            }else{
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.incorrectPhoneNumber());
            }
            if(!phone_number.matches("[+][0-9]{11}")){
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.incorrectPhoneNumber());
            }
            TokenParser.ACCOUNT type = TokenParser.ACCOUNT.valueOf(account_type);
            Account account = getAccount(UUID, password, type);
            if(account != null){
                return error(statusCodeCreator.alreadyRegistered());
            }
            Date now = Date.from(Instant.now());

            if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
                TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
                Host host = new Host(UUID, name, email, phone_number, password, data);
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(data, HttpStatus.OK);
            }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
                Participator participator = new Participator(UUID, name, email, phone_number, password, data);
                participatorRepository.saveAndFlush(participator);

                return new ResponseEntity<>(data, HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }


    @PostMapping("/app/login")
    public ResponseEntity<TokenData> login(
            @RequestHeader String UUID,
            @RequestHeader String password,
            @RequestParam String account_type
    ){
        try{

            Date now = Date.from(Instant.now());
            if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_HOST.name())){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);

                if(host == null){
                    return error(statusCodeCreator.userNotFound());
                }
                TokenData tokenData = tokenParser.createTokenData(host.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
                host.setAccess_token(tokenData.getAccess_token());
                host.setRefresh_token(tokenData.getRefresh_token());
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);

            }else if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name())){

                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return error(statusCodeCreator.userNotFound());
                }
                TokenData tokenData = tokenParser.createTokenData(participator.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
                participator.setAccess_token(tokenData.getAccess_token());
                participator.setRefresh_token(tokenData.getRefresh_token());
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<HashMap<String, String>> checkIfRegistered(@RequestParam String UUID){
        try{
            Host host = hostRepository.getByUuid(UUID);
            Manager manager = managerRepository.getByUuid(UUID);
            Participator participator = participatorRepository.getByUuid(UUID);
            HashMap<String, String> resp = new HashMap<>();
            resp.put("host", (host == null ? "Not found" : "Found"));
            resp.put("manager", (manager == null ? "Not found" : "Found"));
            resp.put("participator", (participator == null ? "Not found" : "Found"));
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }


//    @PostMapping("/app/update_credentials")
//    public ResponseEntity<String> updateHostCredentials(
//            @RequestHeader String access_token,
//            @RequestParam(name = "name", required = false, defaultValue = "") String name,
//            @RequestParam(name = "email", required = false, defaultValue = "")String email,
//            @RequestParam(name = "phone_number", required = false, defaultValue = "")String phone_number
//    ){
//        try{
//
//            TokenParser.ACCOUNT account = tokenParser.getAccountType(access_token);
//            if(tokenParser.getType(access_token) != TokenParser.TYPE.ACCESS){
//                return new ResponseEntity<String>("TOKEN invalid", statusCodeCreator.tokenNotValid());
//
//            }
//            String UUID = tokenParser.getUUID(access_token);
//            String password = tokenParser.getPassword(access_token);
//
//            if(account == TokenParser.ACCOUNT.ACCOUNT_HOST){
//                Host host = hostRepository.findByUuidAndPassword(UUID, password);
//                if( host == null){
//                    return new ResponseEntity<String>("TOKEN invalid",  statusCodeCreator.userNotFound());
//                }
//                if(name.length() > 1){
//                    host.setName(name);
//                }
//                if(email.length() > 1){
//                    host.setEmail(email);
//                }
//                if(phone_number.length() > 1){
//                    host.setPhoneNumber(phone_number);
//                }
//                hostRepository.saveAndFlush(host);
//                return new ResponseEntity<>("Updated", HttpStatus.OK);
//            }else if(account == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
//                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
//                if(participator == null){
//                    return new ResponseEntity<String>("TOKEN invalid", statusCodeCreator.userNotFound());
//                }
//                if(name.length() > 1){
//                    participator.setName(name);
//                }
//                if(email.length() > 1){
//                    participator.setEmail(email);
//                }
//                if(phone_number.length() > 1){
//                    participator.setPhoneNumber(phone_number);
//                }
//                participatorRepository.saveAndFlush(participator);
//                return new ResponseEntity<>("Updated", HttpStatus.OK);
//            }else{
//                return new ResponseEntity<String>("Wrong account type", statusCodeCreator.missingAccountTypeField());
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseEntity<String>("TOKEN invalid", statusCodeCreator.serverError());
//        }
//    }

    @GetMapping("/app/refresh")
    public ResponseEntity<TokenData> updateAccessToken(
            @RequestHeader String refresh_token
    ){
        try{
            TokenParser.ACCOUNT account_type = tokenParser.getAccountType(refresh_token);
            TokenParser.TYPE token_type = tokenParser.getType(refresh_token);
            if(token_type != TokenParser.TYPE.REFRESH){
                return error(statusCodeCreator.tokenNotValid());
            }
            String UUID = tokenParser.getUUID(refresh_token);
            String password = tokenParser.getPassword(refresh_token);

            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, Date.from(Instant.now()), account_type);
            if (account_type == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);
                if(host == null){
                    return error(statusCodeCreator.userNotFound());
                }
                host.setAccess_token(access_token);
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(tokenParser.getTokenData(host), HttpStatus.OK);
            }else if(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return error(statusCodeCreator.userNotFound());
                }
                participator.setAccess_token(access_token);
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<>(tokenParser.getTokenData(participator), HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/find_party")
    public ResponseEntity<Set<PartyData>> getPartyList(
            @RequestHeader String access_token,
            @RequestParam String code_word
    ){
        try{
            Date broadcast_start = Date.from(Instant.now().minusSeconds(secretDataLoader.getPartyBroadcastDuration()));
            Set<Party> partyList = partyRepository.getAllByBroadcastWord(code_word);
            if(partyList == null){
                return new ResponseEntity<>(new HashSet<PartyData>(), HttpStatus.OK);
            }
            return  new ResponseEntity<>(PartyData.createPartyData(partyList), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/find_parties_by_code_words")
    public ResponseEntity<Set<PartyData>> findPartiesByCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Set<Party> parties = partyRepository.getAllByBroadcastWordIn(participator.getCodeWordsStrings());
            return new ResponseEntity<>(PartyData.createPartyData(parties), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/participator/join_party")
    public ResponseEntity<String> joinParty(
            @RequestParam Integer party_id,
            @RequestParam String code_word,
            @RequestHeader String access_token
    ){
        try{
            Party party = partyRepository.findByParty(party_id);
            if(party == null){
                return new ResponseEntity<>("Not found", statusCodeCreator.userNotFound());
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
                return new ResponseEntity<>("Code word mismatch", statusCodeCreator.codeWordMismatch());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }

    }

    @GetMapping("/app/participator/my_party_list")
    public ResponseEntity<Set<PartyData>> getPartiesByParticipator(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            return new ResponseEntity<>(PartyData.createPartyData(participator.getParties()), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }

    }

    @PostMapping("/app/participator/upload_code_words")
    public ResponseEntity<String> uploadCodeWordsForParticipator(
            @RequestHeader String access_token,
            @RequestBody List<String> code_words
            ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>("", statusCodeCreator.userNotFound());
            }
            int initCount = participator.getCode_words().size();
            participator.addCodeWords(code_words);
            int endCount = participator.getCode_words().size();
            return new ResponseEntity<>("Added " + Integer.toString(endCount - initCount), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping
    public ResponseEntity<String> logout(@RequestHeader String refresh_token){
        try{
            if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = (Host)getAccount(refresh_token);
                TokenData t = tokenParser.createTokenData(host.getUuid(), host.getPassword(), TokenParser.ACCOUNT.ACCOUNT_HOST,Date.from(Instant.now()));
                host.setAccess_token(t.getAccess_token());
                host.setRefresh_token(t.getRefresh_token());
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator p = (Participator)getAccount(refresh_token);
                TokenData t = tokenParser.createTokenData(p.getUuid(), p.getPassword(), TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR,Date.from(Instant.now()));
                p.setAccess_token(t.getAccess_token());
                p.setRefresh_token(t.getRefresh_token());
                participatorRepository.saveAndFlush(p);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Some thing went wrong", statusCodeCreator.tokenNotValid());
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Some thing went wrong", statusCodeCreator.serverError());
        }
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
