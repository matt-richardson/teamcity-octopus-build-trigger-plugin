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
        this.statusMap = new ArrayList<>();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {
            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    statusMap.add(Release.Parse(pair));
                }
            }
        }
    }

    public Releases(Release oldRelease) {
        this(oldRelease.toString());
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

    public boolean contains(Release other) {
        for (Release release: statusMap) {
            if (release.id.equals(other.id))
                return true;
        }
        return false;
    }

    public void add(Releases releases) {
        for (Release release: releases.statusMap) {
            if (!contains(release)) {
                add(release);
            }
        }
    }

    public void add(Release release) {
        if (release.getClass() != NullRelease.class && !contains(release))
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

    public int size() {
        return statusMap.size();
    }

    public Release[] toArray() {
        return statusMap.toArray(new Release[0]);
    }
}
