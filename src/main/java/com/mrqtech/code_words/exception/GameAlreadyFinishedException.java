package com.mrqtech.code_words.exception;

public class GameAlreadyFinishedException extends RuntimeException {
    public GameAlreadyFinishedException(String message) {
        super(message);
    }
}
