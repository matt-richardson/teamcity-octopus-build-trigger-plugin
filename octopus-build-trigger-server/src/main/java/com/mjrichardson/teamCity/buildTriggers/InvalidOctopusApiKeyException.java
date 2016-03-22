package com.mjrichardson.teamCity.buildTriggers;

public class InvalidOctopusApiKeyException extends Exception {
    public final int code;

    public InvalidOctopusApiKeyException(int code, String reason) {
        super("Server returned " + code + " " + reason);
        this.code = code;
    }
}
