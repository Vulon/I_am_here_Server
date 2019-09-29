package com.I_am_here.Controllers;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Party;
import com.I_am_here.Database.Entity.Subject;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;


@RestController
public class WebRestController {

    private ManagerRepository managerRepository;
    private TokenParser tokenParser;

    public WebRestController(ManagerRepository managerRepository, TokenParser tokenParser) {
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
    }

    @PostMapping("/web/login")
    @ResponseBody
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password){
        Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
        System.out.println("Entered /web/login");
        if(manager == null){
            System.out.println("Manager was not found");
            return new ResponseEntity<TokenData>(new TokenData(), HttpStatus.CONFLICT);
        }
        String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, Date.from(Instant.now()), TokenParser.ACCOUNT.ACCOUNT_MANAGER);
        String refresh_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.REFRESH, Date.from(Instant.now()), TokenParser.ACCOUNT.ACCOUNT_MANAGER);
        TokenData tokenData = new TokenData(access_token, refresh_token, tokenParser.getExpitaionDate(access_token), tokenParser.getExpitaionDate(refresh_token));
        manager.setAccess_token(access_token);
        manager.setRefresh_token(refresh_token);
        System.out.println("Entered /web/login, tokenData: " + tokenData.toString());

        return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);

    }

    @GetMapping("web/test")
    public ResponseEntity<String> securedPath(){
        return new ResponseEntity<String>("Response", HttpStatus.OK);
    }


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

        String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, now, TokenParser.ACCOUNT.ACCOUNT_MANAGER);
        String refresh_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.REFRESH, now, TokenParser.ACCOUNT.ACCOUNT_MANAGER);

        manager = new Manager(UUID, name, email, phone_number, password, access_token, refresh_token,
                new HashSet<Subject>(), new HashSet<Host>(), new HashSet<Party>());
        System.out.println("Saving manager + " + manager.toString());
        managerRepository.saveAndFlush(manager);
        TokenData tokenData = new TokenData(access_token, refresh_token, tokenParser.getExpitaionDate(access_token),
                tokenParser.getExpitaionDate(refresh_token));
        return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
    }



    @PostMapping("/web/logout")
    @ResponseBody
    public ResponseEntity<String> logout(){
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>("Logged out", HttpStatus.OK);
    }
}
