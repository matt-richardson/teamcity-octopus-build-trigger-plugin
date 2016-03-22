package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

class NoChangedEnvironmentsException extends Exception {
    public NoChangedEnvironmentsException(Environments oldEnvironments, Environments newEnvironments) {
        super(String.format("Didn't find any differences between '%s' and '%s'.",
                oldEnvironments.toString(), newEnvironments.toString()));
    }
}
