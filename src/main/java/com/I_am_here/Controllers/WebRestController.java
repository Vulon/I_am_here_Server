package com.I_am_here.Controllers;

import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebRestController {

    private ManagerRepository managerRepository;
    private ParticipatorRepository participatorRepository;
    private HostRepository hostRepository;

    public WebRestController(ManagerRepository managerRepository, ParticipatorRepository participatorRepository, HostRepository hostRepository) {
        this.managerRepository = managerRepository;
        this.participatorRepository = participatorRepository;
        this.hostRepository = hostRepository;
    }

    @PostMapping("/login/auth")
    @ResponseBody //TODO make web login
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password){


        return null;
    }

    @PostMapping("/login/logout")
    @ResponseBody
    public ResponseEntity<String> logout(@RequestParam String UUID){

        return null;
    }
}
