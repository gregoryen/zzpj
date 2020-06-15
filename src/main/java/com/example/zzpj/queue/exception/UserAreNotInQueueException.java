package com.example.zzpj.queue.exception;

public class UserAreNotInQueueException extends GameQueueException{
    public UserAreNotInQueueException() {
        super();
    }

    public UserAreNotInQueueException(String text) {
        super(text);
    }
}
