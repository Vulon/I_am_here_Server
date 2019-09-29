package com.I_am_here.Controllers;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Party;
import com.I_am_here.Database.Entity.Subject;
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
public class WebRestController {

    private ManagerRepository managerRepository;
    private TokenParser tokenParser;

    public WebRestController(ManagerRepository managerRepository, TokenParser tokenParser) {
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
    }

    @PostMapping("/web/login")
    @ResponseBody //TODO make web login
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password){

        System.out.println("SOME ONE TRIES TO LOGIN! " + UUID + " " + password);
        return null;
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

        String access_token = tokenParser.CreateToken(UUID, password, TokenParser.TYPE.ACCESS, now, TokenParser.ACCOUNT.ACCOUNT_MANAGER);
        String refresh_token = tokenParser.CreateToken(UUID, password, TokenParser.TYPE.REFRESH, now, TokenParser.ACCOUNT.ACCOUNT_MANAGER);

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
    public ResponseEntity<String> logout(@RequestParam String UUID){

        return null;
    }
}
