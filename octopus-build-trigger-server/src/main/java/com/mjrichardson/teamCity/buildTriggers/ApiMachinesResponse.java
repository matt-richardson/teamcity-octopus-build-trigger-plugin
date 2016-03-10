package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machine;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machines;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;

public class ApiMachinesResponse {
    public Machines machines;
    public String nextLink;

    public ApiMachinesResponse(String machinesResponse) throws ParseException {
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(machinesResponse);

        machines = new Machines();

        List items = (List) response.get("Items");
        for (Object item : items) {
            machines.add(Machine.Parse((Map) item));
        }

        Object nextPage = ((Map) response.get("Links")).get("Page.Next");
        if (nextPage != null)
            nextLink = nextPage.toString();
    }
}
