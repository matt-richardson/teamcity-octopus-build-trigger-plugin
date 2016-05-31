package com.mjrichardson.teamCity.buildTriggers.Exceptions;

public class UnexpectedResponseCodeException extends Exception {
    public final int code;
    public final String reason;

    public UnexpectedResponseCodeException(int code, String reason) {
        super("Server returned " + code + " " + reason);
        this.code = code;
        this.reason = reason;
    }
}

