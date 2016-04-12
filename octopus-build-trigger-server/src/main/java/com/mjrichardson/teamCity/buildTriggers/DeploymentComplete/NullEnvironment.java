package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;

public class NullEnvironment extends Environment {
    public NullEnvironment() {
        super("", new NullOctopusDate(), new NullOctopusDate(), "", "", "", "");
    }
}
