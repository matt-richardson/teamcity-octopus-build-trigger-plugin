package com.mjrichardson.teamCity.buildTriggers;

import java.util.ArrayList;
import java.util.Iterator;

//todo: move under 'Model' namespace?
public class Deployments implements Iterable<Deployment> {
    private ArrayList<Deployment> statusMap;

    public Deployments() {
        this.statusMap = new ArrayList<>();
    }

    public void add(Deployment deployment) {
        if (!contains(deployment.environmentId))
            statusMap.add(deployment);
    }

    public boolean contains(String octopusDeploymentId) {
        for (Deployment deployment : statusMap) {
            if (deployment.id.equals(octopusDeploymentId))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<Deployment> iterator() {
        return statusMap.iterator();
    }

    public int size() {
        return statusMap.size();
    }

    public Deployment[] toArray() {
        return statusMap.toArray(new Deployment[0]);
    }
}
