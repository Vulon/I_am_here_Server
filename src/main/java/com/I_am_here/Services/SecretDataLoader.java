package com.I_am_here.Services;

import com.I_am_here.Application;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SecretDataLoader {

    private Environment env;

    public SecretDataLoader(Environment env) {
        this.env = env;
    }

    private String secret_key;

    private String spring_security_key;

    public void saveSecretKey(){
        ClassLoader classLoader = Application.class.getClassLoader();
        File file = new File(classLoader.getResource("data.properties").getFile());
        try(FileOutputStream outputStream = new FileOutputStream(file)){

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.write("OUR OWN SECRET KEY".getBytes());
            objectOutputStream.write("sfdkl jdklfw 24324".getBytes());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public long getAccessTokenValidity(){
        long seconds = Long.parseLong(env.getProperty("access_token_life_seconds"));
        return seconds;
    }

    public long getRefreshTokenValidity(){
        long seconds = Long.parseLong(env.getProperty("refresh_token_life_seconds"));
        return seconds;
    }

    public void loadData(){
        saveSecretKey();
        ClassLoader classLoader = Application.class.getClassLoader();
        File file = new File(classLoader.getResource("data.properties").getFile());
        try(FileInputStream inputStream = new FileInputStream(file)){
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            secret_key = (String)objectInputStream.readObject();
            spring_security_key = (String)objectInputStream.readObject();

        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getSecretTokenKey(){
        //TODO DELETE SECRET KEY SAVING FUNCTION LATER
        loadData();

        return secret_key;
    }

    public String getSpring_security_key(){
        loadData();
        return spring_security_key;
    }
}
