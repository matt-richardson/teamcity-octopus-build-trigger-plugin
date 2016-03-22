package com.mjrichardson.teamCity.buildTriggers;

import java.net.URI;

public class InvalidOctopusUrlException extends Exception {
    public final URI octopusUrl;

    public InvalidOctopusUrlException(URI octopusUrl) {
        super("Unable to connect to octopus at " + octopusUrl);
        this.octopusUrl = octopusUrl;
    }

    public InvalidOctopusUrlException(URI octopusUrl, Exception e) {
        super("Unable to connect to octopus at " + octopusUrl, e);
        this.octopusUrl = octopusUrl;
    }
}
