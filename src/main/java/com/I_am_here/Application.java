package com.I_am_here;


import com.I_am_here.Firebase.FireBaseMessenger;
import com.I_am_here.Security.TokenParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Application {
    public static TokenParser tokenParser;
    public static FireBaseMessenger fireBaseMessenger;

    /**
    Application starts with this method. Spring creates context, it searches all components and creates objects for them.
    Spring connects to database, starts server listener using tomcat.
    Also implements some interfaces like Repositories
    */
    public static void main(String[] args) {
        FireBaseMessenger fireBaseMessenger = new FireBaseMessenger();
        try{
            fireBaseMessenger.init();
        }catch (IOException e){
            e.printStackTrace();
        }

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        tokenParser = context.getBean(TokenParser.class);
    }
}
