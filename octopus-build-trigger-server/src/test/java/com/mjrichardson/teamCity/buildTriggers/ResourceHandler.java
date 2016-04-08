package com.mjrichardson.teamCity.buildTriggers;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceHandler {
    public static String getResource(String name) throws IOException {
        final String resourceName = "/responses/3.3.0/" + name + ".json";
        InputStream resource = ResourceHandler.class.getResourceAsStream(resourceName);
        return IOUtils.toString(resource);
    }
}
