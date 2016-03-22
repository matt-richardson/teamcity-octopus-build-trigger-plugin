package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReleaseCreatedSpec {
    @NotNull
    private final String url;
    @NotNull
    final String projectId;
    @Nullable
    final String version;
    @Nullable
    final String releaseId;

    ReleaseCreatedSpec(@NotNull String url, @NotNull String projectId) {
        this(url, projectId, null, null);
    }

    ReleaseCreatedSpec(@NotNull String url, @NotNull Release release) {
        this(url, release.projectId, release.version, release.releaseId);
    }

    ReleaseCreatedSpec(@NotNull String url, @NotNull String projectId, @Nullable String version, @Nullable String releaseId) {
        this.url = url;
        this.projectId = projectId;
        this.version = version;
        this.releaseId = releaseId;
    }

    public String getRequestorString() {
        if (version == null)
            return String.format("Release of project %s created on %s", projectId, url);
        return String.format("Release %s of project %s created on %s", version, projectId, url);
    }
}
