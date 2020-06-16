package com.example.zzpj.queue.exception;

public class GameQueueNotExistException extends GameQueueException{
    public GameQueueNotExistException() {
        super();
    }

    public GameQueueNotExistException(String text) {
        super(text);
    }
}
