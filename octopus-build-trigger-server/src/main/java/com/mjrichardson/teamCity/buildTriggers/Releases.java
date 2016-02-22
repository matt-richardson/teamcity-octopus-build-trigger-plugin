package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;

public class Releases {
    private ArrayList<Release> statusMap;

    public Releases() {
        this("");
    }

    public Releases(String oldStoredData) {
        this.statusMap = new ArrayList<Release>();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {
            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    statusMap.add(Release.Parse(pair));
                }
            }
        }
    }

    public Releases(Releases oldReleases) {
        this(oldReleases.toString());
    }

    @Override
    public String toString() {
        String result = "";
        Collections.sort(statusMap);
        for (Release release: statusMap) {
            result = String.format("%s%s|", result, release.toString());
        }
        return result.replaceAll("\\|+$", "");
    }

    public boolean isEmpty() {
        return statusMap.size() == 0;
    }

    public Releases trimToOnlyHaveMaximumOneChangedRelease(Releases oldReleases) {
        Releases newReleases = new Releases(oldReleases);

        final String oldStringRepresentation = oldReleases.toString();

        for (Release release: statusMap) {
            newReleases.addOrUpdate(release);
            String newStringRepresentation = newReleases.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return newReleases;
            }
        }

        return newReleases;
    }

    private void addOrUpdate(Release release) {
        if (!contains(release.id)) {
            add(release);
        }
    }

    public boolean overlapsWith(Releases oldReleases) {
        for (Release release: oldReleases.statusMap) {
            if (contains(release.id))
                return true;
        }
        return false;
    }

    public boolean contains(String releaseId) {
        for (Release release: statusMap) {
            if (release.id.equals(releaseId))
                return true;
        }
        return false;
    }

    public void Append(Releases releases) {
        for (Release release: releases.statusMap) {
            if (!contains(release.id)) {
                add(release);
            }
        }
    }

    public void add(Release release) {
        statusMap.add(release);
    }

    public Release getChangedRelease(Releases oldReleases) throws NoChangedReleasesException {
        Releases newReleases = new Releases(oldReleases);
        final String oldStringRepresentation = oldReleases.toString();

        for (Release release: statusMap) {
            newReleases.addOrUpdate(release);
            String newStringRepresentation = newReleases.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return release;
            }
        }
        throw new NoChangedReleasesException(oldReleases, newReleases);
    }
}
