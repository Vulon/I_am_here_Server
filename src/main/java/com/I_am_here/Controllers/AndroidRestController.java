package com.I_am_here.Controllers;

import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class AndroidRestController {


    @PostMapping("/app/login")
    @ResponseBody
    public ResponseEntity<TokenData> login(@RequestParam String UUID, @RequestParam String password){

        System.out.println("SOME ONE TRIES TO LOGIN! " + UUID + " " + password);
        return null;
    }

}
