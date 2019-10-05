package com.I_am_here;


import com.I_am_here.Security.TokenParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static TokenParser tokenParser;

    /**
    Application starts with this method. Spring creates context, it searches all components and creates objects for them.
    Spring connects to database, starts server listener using tomcat.
    Also implements some interfaces like Repositories
    */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        tokenParser = context.getBean(TokenParser.class);
    }
}
