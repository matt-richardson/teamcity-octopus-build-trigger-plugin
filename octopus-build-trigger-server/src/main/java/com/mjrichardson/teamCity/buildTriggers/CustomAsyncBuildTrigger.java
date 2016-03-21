package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.buildTriggers.async.AsyncBuildTrigger;

import java.util.Map;

public interface CustomAsyncBuildTrigger<TItem> extends AsyncBuildTrigger<TItem> {
    Map<String, String> getProperties(TItem item);
}
