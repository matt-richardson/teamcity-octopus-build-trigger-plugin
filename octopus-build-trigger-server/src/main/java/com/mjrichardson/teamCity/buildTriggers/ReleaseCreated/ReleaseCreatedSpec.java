package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReleaseCreatedSpec {
    @NotNull
    private final String url;
    @NotNull
    private final String project; //todo:rename to projectid
    @Nullable
    private final String version;

    ReleaseCreatedSpec(@NotNull String url, @NotNull String project) {
        this(url, project, null);
    }

    ReleaseCreatedSpec(@NotNull String url, @NotNull String project, @Nullable String version) {
        this.url = url;
        this.project = project;
        this.version = version;
    }

    public String getRequestorString() {
        if (version == null)
            return String.format("Release of project %s created on %s", project, url);
        return String.format("Release %s of project %s created on %s", version, project, url);
    }
}
