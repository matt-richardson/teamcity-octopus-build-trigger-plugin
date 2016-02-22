package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.jetbrains.annotations.NotNull;

class ReleaseCreatedSpec {
  @NotNull
  private final String url;
  @NotNull
  private final String project;
  @NotNull
  private final String version;

  ReleaseCreatedSpec(@NotNull String url, @NotNull String project) {
    this(url, project, null);
  }

  ReleaseCreatedSpec(@NotNull String url, @NotNull String project, @NotNull String version) {
    this.url = url;
    this.project = project;
    this.version = version;
  }

  public String getRequestorString() {
    if (version == null)
      return String.format("Release of %s created on %s", project, url);
    return String.format("Release %s of project %s created on %s", version, project, url);
  }
}
