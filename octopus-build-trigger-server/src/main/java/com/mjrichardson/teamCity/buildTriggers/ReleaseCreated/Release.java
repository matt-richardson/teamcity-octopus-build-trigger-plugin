package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;

import java.util.Map;

public class Release implements Comparable {
    final String id;
    final OctopusDate assembledDate;
    final String version;

    public Release(Map item) {
        this.id = item.get("Id").toString();
        this.assembledDate = new OctopusDate(item.get("Assembled").toString());
        this.version = item.get("Version").toString();
    }

    Release(String releaseId, OctopusDate assembledDate, String version) {
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
        final OctopusDate assembledDate = new OctopusDate(split[1]);
        final String version = split[2];

        return new Release(releaseId, assembledDate, version);
    }

    @Override
    public int compareTo(Object o) {
        return ((Release)o).assembledDate.compareTo(assembledDate);
    }
}
