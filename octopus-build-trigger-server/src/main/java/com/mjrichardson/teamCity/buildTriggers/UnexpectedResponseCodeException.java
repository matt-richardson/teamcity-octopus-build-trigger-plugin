package com.mjrichardson.teamCity.buildTriggers;

public class UnexpectedResponseCodeException extends Exception {
    public final int code;

    public UnexpectedResponseCodeException(int code, String reason) {
        super("Server returned " + code + " " + reason);
        this.code = code;
    }
}

