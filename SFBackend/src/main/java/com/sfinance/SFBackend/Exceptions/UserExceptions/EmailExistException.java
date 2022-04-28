package com.sfinance.SFBackend.Exceptions.UserExceptions;

public class EmailExistException extends Exception{
    public EmailExistException(String message) {
        super(message);
    }
}
