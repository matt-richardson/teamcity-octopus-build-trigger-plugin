<%--
  ~ Copyright 2000-2013 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ include file="/include.jsp" %>
<%@ page import="matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>

<tr class="noBorder" >
    <td colspan="2">
        <em>Octopus Deploy build trigger will add a build to the queue when a new successful deployment has completed.</em>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>">Octopus Server Url: <l:star/></label></th>
    <td>
      <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>" className="longField"/>
      <span class="smallNote">
          e.g. https://example.org
      </span>
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>">API Key: </label></th>
    <td>
       <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"/>
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"></span>
    </td>
</tr>

<!-- TODO: add javascript to query OD and get back project list -->
<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_PROJECT%>">Project ID: </label></th>
    <td>
      <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_PROJECT%>"/>
      <span class="smallNote">
          e.g. projects-22
      </span>
      <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_PROJECT%>"></span>
    </td>
</tr>


<!-- TODO: add optional environment dropdown + javascript to query OD and get back list -->
