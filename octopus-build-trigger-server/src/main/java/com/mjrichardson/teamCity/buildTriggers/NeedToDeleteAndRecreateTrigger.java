package com.mjrichardson.teamCity.buildTriggers;

public class NeedToDeleteAndRecreateTrigger extends Exception {
    public NeedToDeleteAndRecreateTrigger() {
        super("The way state is stored has been changed between your current version and the 2.x series.\n" +
              "Please delete the trigger in TeamCity and re-add it.");
    }
}
