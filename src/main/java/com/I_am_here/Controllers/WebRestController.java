package com.I_am_here.Controllers;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Party;
import com.I_am_here.Database.Entity.Subject;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.PartyRepository;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;


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
    private TokenParser tokenParser;

    public WebRestController(ManagerRepository managerRepository, PartyRepository partyRepository, TokenParser tokenParser) {
        this.managerRepository = managerRepository;
        this.partyRepository = partyRepository;
        this.tokenParser = tokenParser;
    }

    /**
     * This method is used to get new tokens. If everything is fine it should return response with new TokenData
     * and status code 200. If server can't find such Manager in Database, it returns status code 409
     */
    @PostMapping("/web/login")
    @ResponseBody
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password){
        Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
        System.out.println("Entered /web/login");
        if(manager == null){
            System.out.println("Manager was not found");
            return new ResponseEntity<TokenData>(new TokenData(), HttpStatus.CONFLICT);
        }
        TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, Date.from(Instant.now()));

        manager.setAccess_token(data.getAccess_token());
        manager.setRefresh_token(data.getRefresh_token());
        managerRepository.saveAndFlush(manager);
        System.out.println("Entered /web/login, tokenData: " + data.toString());

        return new ResponseEntity<TokenData>(data, HttpStatus.OK);

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
            @RequestParam String UUID,
            @RequestParam String password,
            @RequestParam(name = "name", defaultValue = "Manager", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email,
            @RequestParam(name = "phone_number", defaultValue = "", required = false) String phone_number){
        Manager manager = managerRepository.getByUuid(UUID);
        if(manager != null){
            return new ResponseEntity<>(new TokenData(), HttpStatus.CONFLICT);
        }
        Date now = Date.from(Instant.now());
        TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, now);
        manager = new Manager(UUID, name, email, phone_number, password, data.getAccess_token(), data.getRefresh_token(),
                new HashSet<Subject>(), new HashSet<Host>(), new HashSet<Party>());
        System.out.println("Saving manager + " + manager.toString());
        managerRepository.saveAndFlush(manager);
        return new ResponseEntity<TokenData>(data, HttpStatus.OK);
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
                return new ResponseEntity<>("Name of a party should be unique for a single user", HttpStatus.CONFLICT);
            }
            Party newParty = new Party(name, description, broadcast_word, manager);
            newParty = partyRepository.saveAndFlush(newParty);
            manager.addParty(newParty);
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<>("Created " + name, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Request handle error", HttpStatus.I_AM_A_TEAPOT);
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
