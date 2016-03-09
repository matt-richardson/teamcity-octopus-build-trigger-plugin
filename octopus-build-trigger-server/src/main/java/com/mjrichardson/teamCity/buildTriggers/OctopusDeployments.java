package com.mjrichardson.teamCity.buildTriggers;

import java.util.ArrayList;
import java.util.Iterator;

//todo: needs tests
public class OctopusDeployments implements Iterable<OctopusDeployment> {
    private ArrayList<OctopusDeployment> statusMap;

    public OctopusDeployments() {
        this.statusMap = new ArrayList<>();
    }

    public void add(OctopusDeployment octopusDeployment) {
        if (!contains(octopusDeployment.environmentId))
            statusMap.add(octopusDeployment);
    }

    public boolean contains(String octopusDeploymentId) {
        for (OctopusDeployment octopusDeployment : statusMap) {
            if (octopusDeployment.id.equals(octopusDeploymentId))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<OctopusDeployment> iterator() {
        return statusMap.iterator();
    }

    public int size() {
        return statusMap.size();
    }

    public OctopusDeployment[] toArray() {
        return statusMap.toArray(new OctopusDeployment[0]);
    }
}
