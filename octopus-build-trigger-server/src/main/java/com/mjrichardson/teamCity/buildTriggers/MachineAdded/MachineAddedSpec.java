/*
 * Copyright 2016 Matt Richardson.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Based on code graciously open sourced by JetBrains s.r.o
 * (http://svn.jetbrains.org/teamcity/plugins/url-build-trigger/trunk/url-build-trigger-server/src/jetbrains/buildServer/buildTriggers/url/UrlBuildTrigger.java)
 *
 * Original licence:
 *
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.jetbrains.annotations.NotNull;

class MachineAddedSpec {
    @NotNull
    private final String url;
    @NotNull
    final String machineName;
    @NotNull
    final String machineId;
    @NotNull
    final String environmentIds;
    @NotNull
    final String roleIds;

    private MachineAddedSpec(@NotNull String url,
                     @NotNull String machineName,
                     @NotNull String machineId,
                     @NotNull String[] environmentIds,
                     @NotNull String[] roleIds) {
        this.url = url;
        this.machineName = machineName;
        this.machineId = machineId;
        this.environmentIds = String.join(",", environmentIds);
        this.roleIds = String.join(",", roleIds);
    }

    public MachineAddedSpec(String octopusUrl, Machine machine) {
        this(octopusUrl, machine.name, machine.id , machine.environmentIds, machine.roleIds);
    }

    public String getRequestorString() {
        return String.format("Machine %s added to %s", machineName, url);
    }

    @Override
    public String toString() {
        return "{ " +
                  "url: '" + url + "', " +
                  "machineName: '" + machineName + "', " +
                  "machineId: '" + machineId + "', " +
                  "environmentIds: '" + environmentIds + "', " +
                  "roleIds: '" + roleIds + "'" +
               " }";
    }
}
