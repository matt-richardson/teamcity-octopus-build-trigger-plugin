package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;

import java.util.Map;

public class Release implements Comparable<Release> {
    public final String id;
    public final OctopusDate assembledDate;
    public final String version;

    public Release(Map item) {
        this.id = item.get("Id").toString();
        this.assembledDate = OctopusDate.Parse(item.get("Assembled").toString());
        this.version = item.get("Version").toString();
    }

    public Release(String releaseId, OctopusDate assembledDate, String version) {
        this.id = releaseId;
        this.assembledDate = assembledDate;
        this.version = version;
    }

    @Override
    public String toString() {
        return id + ";" + assembledDate.toString() + ";" + version;
    }

    public static Release Parse(String pair) {
        if (pair == null || pair == "") {
            return new NullRelease();
        }
        final String[] split = pair.split(";");
        final String releaseId = split[0];
        final OctopusDate assembledDate = OctopusDate.Parse(split[1]);
        final String version = split[2];

        return new Release(releaseId, assembledDate, version);
    }

    @Override
    public int compareTo(Release o) {
        return o.assembledDate.compareTo(assembledDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != Release.class && obj.getClass() != NullRelease.class)
            return false;
        return toString().equals(obj.toString());
    }
}
