package com.mjrichardson.teamCity.buildTriggers.Model;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class ApiTaskResponse {
    public Boolean isCompleted;
    public Boolean finishedSuccessfully;

    public ApiTaskResponse(String taskResponse) throws ParseException {
        JSONParser parser = new JSONParser();

        Map task = (Map) parser.parse(taskResponse);

        isCompleted = Boolean.parseBoolean(task.get("IsCompleted").toString());
        finishedSuccessfully = Boolean.parseBoolean(task.get("FinishedSuccessfully").toString());
    }
}
