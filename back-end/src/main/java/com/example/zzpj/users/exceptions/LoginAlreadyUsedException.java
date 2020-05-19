package com.example.zzpj.users.exceptions;

public class LoginAlreadyUsedException extends UserException {
    public LoginAlreadyUsedException() {
        super();
    }

    public LoginAlreadyUsedException(String message) {
        super(message);
    }
}
