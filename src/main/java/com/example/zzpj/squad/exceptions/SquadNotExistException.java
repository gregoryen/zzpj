package com.example.zzpj.squad.exceptions;

public class SquadNotExistException extends SquadException{
    public SquadNotExistException() {
        super();
    }

    public SquadNotExistException(String message) {
        super(message);
    }
}
