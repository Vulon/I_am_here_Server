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

    private HashMap<String, String> secretMap;

    public void saveSecretKey(){
        ClassLoader classLoader = Application.class.getClassLoader();
        File file = new File(classLoader.getResource("data.properties").getFile());
        try(FileOutputStream outputStream = new FileOutputStream(file)){

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            secretMap = new HashMap<>();
            secretMap.put("secret_key", "OUR OWN SECRET KEY");
            secretMap.put("spring_security_key", "sfdkl jdklfw 24324");
            objectOutputStream.writeObject(secretMap);

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
            secretMap = (HashMap<String, String>) objectInputStream.readObject();

        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getSecretTokenKey(){
        //TODO DELETE SECRET KEY SAVING FUNCTION LATER
        loadData();

        return secretMap.get("secret_key");
    }

    public String getSpring_security_key(){
        loadData();
        return secretMap.get("spring_security_key");
    }
}
