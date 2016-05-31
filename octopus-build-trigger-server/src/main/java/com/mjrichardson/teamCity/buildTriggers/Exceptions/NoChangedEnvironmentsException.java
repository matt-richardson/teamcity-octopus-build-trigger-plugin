package com.mjrichardson.teamCity.buildTriggers.Exceptions;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environments;

public class NoChangedEnvironmentsException extends Exception {
    public NoChangedEnvironmentsException(Environments oldEnvironments, Environments newEnvironments) {
        super(String.format("Didn't find any differences between '%s' and '%s'.",
                oldEnvironments.toString(), newEnvironments.toString()));
    }
}
