package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;

//todo: try a mocking framework to cleanup these fakes
public class FakeEventDispatcher extends EventDispatcher<BuildServerListener> {
    public FakeEventDispatcher() {
        super(BuildServerListener.class);
    }
}
