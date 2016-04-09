package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;

public class NullRelease extends Release {
    public NullRelease() {
        super("", new NullOctopusDate(), "", "");
    }
}
