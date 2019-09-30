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
        TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, Date.from(Instant.now()));

        manager.setAccess_token(data.getAccess_token());
        manager.setRefresh_token(data.getRefresh_token());
        managerRepository.saveAndFlush(manager);
        System.out.println("Entered /web/login, tokenData: " + data.toString());

        return new ResponseEntity<TokenData>(data, HttpStatus.OK);

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
        TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, now);

        manager = new Manager(UUID, name, email, phone_number, password, data.getAccess_token(), data.getRefresh_token(),
                new HashSet<Subject>(), new HashSet<Host>(), new HashSet<Party>());
        System.out.println("Saving manager + " + manager.toString());
        managerRepository.saveAndFlush(manager);

        return new ResponseEntity<TokenData>(data, HttpStatus.OK);
    }



    @PostMapping("/web/logout")
    @ResponseBody
    public ResponseEntity<String> logout(){
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>("Logged out", HttpStatus.OK);
    }
}
