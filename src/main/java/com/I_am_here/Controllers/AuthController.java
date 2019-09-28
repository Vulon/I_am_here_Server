package com.I_am_here.Controllers;


import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Repository.HostRepository;
import com.I_am_here.Database.Repository.ManagerRepository;
import com.I_am_here.Database.Repository.ParticipatorRepository;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private ManagerRepository managerRepository;
    private ParticipatorRepository participatorRepository;
    private HostRepository hostRepository;

    public AuthController(ManagerRepository managerRepository, ParticipatorRepository participatorRepository, HostRepository hostRepository) {
        this.managerRepository = managerRepository;
        this.participatorRepository = participatorRepository;
        this.hostRepository = hostRepository;
    }

    @PostMapping("/login/auth")
    @ResponseBody //TODO do this later
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password, @RequestParam String account_type){
        if(account_type.equals("host")){
            Host host = hostRepository.findByUUIDAndPassword(UUID, password);

        }else if(account_type.equals("manager")){

        }else if(account_type.equals("participator")){

        }else{
            return new ResponseEntity<TokenData>(new TokenData(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<TokenData>()
    }
}
