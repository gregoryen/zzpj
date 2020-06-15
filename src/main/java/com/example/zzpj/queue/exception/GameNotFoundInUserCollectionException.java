package com.example.zzpj.queue.exception;

public class GameNotFoundInUserCollectionException extends GameQueueException {
    public GameNotFoundInUserCollectionException() {
        super();
    }

    public GameNotFoundInUserCollectionException(String text) {
        super(text + " not found in user collection");
    }
}
