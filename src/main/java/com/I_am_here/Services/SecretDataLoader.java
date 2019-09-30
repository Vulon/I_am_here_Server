package com.I_am_here.Services;

import com.I_am_here.Application;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

@Service
public class SecretDataLoader {

    private Environment env;

    public SecretDataLoader(Environment env) {
        this.env = env;
    }



    public long getAccessTokenValidity(){
        long seconds = Long.parseLong(env.getProperty("access_token_life_seconds"));
        return seconds;
    }

    public long getRefreshTokenValidity(){
        long seconds = Long.parseLong(env.getProperty("refresh_token_life_seconds"));
        return seconds;
    }


    public String getSecretTokenKey(){
        return env.getProperty("secret_token_key");
    }

    public String getSpring_security_key(){

        return env.getProperty("spring_security_key");
    }
}
