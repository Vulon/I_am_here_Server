package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.PartyRepository;
import com.I_am_here.Database.Repository.SubjectRepository;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.StatusCodeCreator;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;


/**
 * ServerController. Methods of this class get called when server gets requests for urls like /web/*
 * Most of this methods can be called only if a request has a valid access token.
 * Access token has a field describing, what type of Account this token is associated with, so if a user is
 * registered as Host or Participator he can access /web/* routes only if he is also registered as Manager
 */
@RestController
public class WebRestController {

    private ManagerRepository managerRepository;
    private PartyRepository partyRepository;
    private SubjectRepository subjectRepository;
    private TokenParser tokenParser;
    private StatusCodeCreator statusCodeCreator;


    public WebRestController(ManagerRepository managerRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, TokenParser tokenParser, StatusCodeCreator statusCodeCreator) {
        this.managerRepository = managerRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.tokenParser = tokenParser;
        this.statusCodeCreator = statusCodeCreator;
    }

    /**
     * This method is used to get new tokens. If everything is fine it should return response with new TokenData
     * and status code 200. If server can't find such Manager in Database, it returns status code 409
     */
    @PostMapping("/web/login")
    @ResponseBody
    public ResponseEntity<TokenData> login(@RequestHeader String UUID, @RequestHeader String password){
        try{
            Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
            if(manager == null){
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.userNotFound());
            }
            TokenData data = tokenParser.createTokenData(manager.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, Date.from(Instant.now()));

            manager.setAccess_token(data.getAccess_token());
            manager.setRefresh_token(data.getRefresh_token());
            managerRepository.saveAndFlush(manager);
            System.out.println("Entered manager: " + manager);
            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }


    }


    /**
     * This method is used to create new account. It will create Manager account and store data in manager table in  database.
     * Method returns TokenData with new generated tokens and status code 200. If the database contains such user already,
     * server returns status code 409
     * @param name Not necessary
     * @param email Not necessary
     * @param phone_number Not necessary
     */
    @PostMapping("/web/register")
    public ResponseEntity<TokenData> register(
            @RequestHeader String UUID,
            @RequestHeader String password,
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
            Manager manager = managerRepository.getByUuid(UUID);
            if(manager != null){
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.alreadyRegistered());
            }
            Date now = Date.from(Instant.now());
            TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, now);
            manager = new Manager(UUID, name, email, phone_number, password, data.getAccess_token(), data.getRefresh_token(),
                    new HashSet<Subject>(), new HashSet<Host>(), new HashSet<Party>());
            System.out.println("Saving manager + " + manager.toString());
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }

    }


    /**
     * Create new Party for that manager. If an error occurs, 418 status is returned.
     * Name of a party should be unique, so if such manager already has a party with that name, server will return 409 error.
     * @param access_token - manager access token
     * @param name - Name of a new party
     * @param broadcast_word - Code word that will be used to enter this party
     * @param description - Optional party description
     */
    @PostMapping("/web/create_party")
    public ResponseEntity<String> createParty(
            @RequestHeader String access_token,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "code") String broadcast_word,
            @RequestParam(name = "description", required = false, defaultValue = "Some event") String description

    ){
        try{
            String UUID = tokenParser.getUUID(access_token);
            Manager manager = managerRepository.getByUuid(UUID);
            Party party = partyRepository.getByNameAndManager(name, manager);
            if(party != null){
                return new ResponseEntity<>("Name of a party should be unique for a single user", statusCodeCreator.notUniqueName());
            }
            Party newParty = new Party(name, description, broadcast_word, manager);
            newParty = partyRepository.saveAndFlush(newParty);
            manager.addParty(newParty);
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<>("Created " + name, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Request handle error", statusCodeCreator.serverError());
        }
    }


    @PostMapping("/web/create_subject")
    public ResponseEntity<String> createSubject(
            @RequestHeader String access_token,
            @RequestParam String name,
            @RequestParam long start_date,
            @RequestParam long finish_date,
            @RequestParam String code_word,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required=false, defaultValue = "0") int plan
    ){
        try{
            Manager manager = managerRepository.findByUuid(tokenParser.getUUID(access_token));
            if(manager == null){
                return new ResponseEntity<>("Not found", statusCodeCreator.userNotFound());
            }
            Date start = new Date();
            start.setTime(start_date);
            Date finish = new Date();
            finish.setTime(finish_date);
            Subject subject = new Subject(name, plan, description, start, finish, code_word, manager);
            subjectRepository.saveAndFlush(subject);
            return new ResponseEntity<>("Subject " + name + " created", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }
    }





    /**
     * Not sure, that this method is required, it clears security context, so server will not have any cookies
     * after this method invokes
     */
    @PostMapping("/web/logout")
    @ResponseBody
    public ResponseEntity<String> logout(){
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>("Logged out", HttpStatus.OK);
    }
}
