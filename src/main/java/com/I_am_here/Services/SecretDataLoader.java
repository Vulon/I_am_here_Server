package com.I_am_here.Services;

import com.I_am_here.Application;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 This class is used to load some constants, from application.properties file
 application.properties file  contains info about server ports, database username,
 password, ip, port
 Class Environment is a Spring class that handles data loading from this file,
 It can also load user-defined data like "access_token_life_seconds"

 For security reasons application.properties is added to .gitignore, it means, that git
 will not include this file to repository.
 If you want to run server, contact me
 */
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

    public long getPartyBroadcastDuration(){
        long seconds = Long.parseLong(env.getProperty("party_broadcast_durration"));
        return seconds;
    }


    /**
     * Get secret_token_key property from application.properties
     * @return Secret token key that is used to sign tokens
     */
    public String getSecretTokenKey(){
        return env.getProperty("secret_token_key");
    }

    public String getSpring_security_key(){

        return env.getProperty("spring_security_key");
    }
}
