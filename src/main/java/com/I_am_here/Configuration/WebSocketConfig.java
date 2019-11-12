package com.I_am_here.Configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketConfig{
        //implements WebSocketMessageBrokerConfigurer {
    //TODO add websockets later

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        BasicAuthenticationEntryPoint entryPoint =
                new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("User Visible Realm");
        return entryPoint;
    }
}
