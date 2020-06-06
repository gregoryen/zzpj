package com.example.zzpj.users.exceptions;

public abstract class UserException extends RuntimeException {
    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }
}
