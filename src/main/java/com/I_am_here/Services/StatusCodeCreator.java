package com.I_am_here.Services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StatusCodeCreator {
    public HttpStatus userNotFound(){
        return HttpStatus.CONFLICT;
    }

    public HttpStatus tokenNotValid(){
        return HttpStatus.NOT_ACCEPTABLE;
    }

    public HttpStatus alreadyRegistered(){
        return HttpStatus.CONFLICT;
    }

    public HttpStatus serverError(){
        return HttpStatus.I_AM_A_TEAPOT;
    }

    public HttpStatus missingAccountTypeField(){
        return HttpStatus.BAD_GATEWAY;
    }

    public HttpStatus codeWordMismatch(){
        return HttpStatus.CONFLICT;
    }

    public HttpStatus incorrectPhoneNumber(){
        return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus notUniqueName(){
        return HttpStatus.CONFLICT;
    }

}
