package com.mjrichardson.teamCity.buildTriggers;

class NoChangedReleasesException extends Exception {
  public NoChangedReleasesException(String message) {
    super(message);
  }

  public NoChangedReleasesException(Releases oldReleases, Releases newReleases) {
    this(String.format("Didn't find any differences between '%s' and '%s'.",
            oldReleases.toString(), newReleases.toString()));
  }
}
