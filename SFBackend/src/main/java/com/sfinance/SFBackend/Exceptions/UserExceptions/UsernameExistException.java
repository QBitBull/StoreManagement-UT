package com.sfinance.SFBackend.Exceptions.UserExceptions;

public class UsernameExistException extends Exception{
    public UsernameExistException(String message) {
        super(message);
    }
}
