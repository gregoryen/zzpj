package com.example.zzpj.users.exceptions;

public class SteamIdAlreadyUsedException extends UserException {
    public SteamIdAlreadyUsedException() {
        super();
    }

    public SteamIdAlreadyUsedException(String message) {
        super(message);
    }
}
