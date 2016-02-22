package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

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

    private void addOrUpdate(Release release) {
        if (!contains(release.id)) {
            add(release);
        }
    }

    public boolean overlapsWith(Release oldRelease) {
        if (contains(oldRelease.id))
            return true;
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

    public Release getNextRelease(Release oldRelease) {
        Collections.sort(statusMap);
        Release nextRelease = oldRelease;

        //for some unknown reason, was getting a concurrent modification exception here when this was a foreach
        for (int i = 0; i < statusMap.size(); i++) {
            Release release = statusMap.get(i);
            if (release.compareTo(oldRelease) < 0)
                nextRelease = release;
            else if (release.version.equals(oldRelease.version))
                return nextRelease;
        }
        return oldRelease;
    }
}
