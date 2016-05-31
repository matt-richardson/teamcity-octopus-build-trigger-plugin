package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.NeedToDeleteAndRecreateTriggerException;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Release implements Comparable<Release> {
    @NotNull
    public final String releaseId;
    @NotNull
    public final OctopusDate assembledDate;
    @NotNull
    public final String version;
    @NotNull
    public final String projectId;

    public Release(@NotNull String releaseId, @NotNull OctopusDate assembledDate, @NotNull String version, @NotNull String projectId) {
        this.releaseId = releaseId;
        this.assembledDate = assembledDate;
        this.version = version;
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return releaseId + ";" + assembledDate.toString() + ";" + version + ";" + projectId;
    }

    public static Release Parse(Map item) {
        String releaseId = item.get("Id").toString();
        OctopusDate assembledDate = OctopusDate.Parse(item.get("Assembled").toString());
        String version = item.get("Version").toString();
        String projectId = item.get("ProjectId").toString();

        return new Release(releaseId, assembledDate, version, projectId);
    }

    public static Release Parse(String pair) throws NeedToDeleteAndRecreateTriggerException {
        if (pair == null || pair.equals("")) {
            return new NullRelease();
        }
        final Integer DONT_REMOVE_EMPTY_VALUES = -1;
        final String[] split = pair.split(";", DONT_REMOVE_EMPTY_VALUES);
        final String releaseId = split[0];
        final OctopusDate assembledDate = OctopusDate.Parse(split[1]);
        final String version = split[2];

        if (split.length < 4)
            throw new NeedToDeleteAndRecreateTriggerException();

        final String projectId = split[3];

        Release result = new Release(releaseId, assembledDate, version, projectId);
        if (result.equals(new NullRelease()))
            return new NullRelease();
        return result;
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
