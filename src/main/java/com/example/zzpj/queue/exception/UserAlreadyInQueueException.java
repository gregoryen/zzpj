package com.example.zzpj.queue.exception;

public class UserAlreadyInQueueException extends GameQueueException{
    public UserAlreadyInQueueException() {
        super();
    }

    public UserAlreadyInQueueException(String text) {
        super(text);
    }
}
