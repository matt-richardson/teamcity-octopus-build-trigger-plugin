<%--
  ~ Copyright 2016 Matt Richardson.
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~
  ~ Based on code graciously open sourced by JetBrains s.r.o
  ~ (http://svn.jetbrains.org/teamcity/plugins/url-build-trigger/trunk/url-build-trigger-server/resource/buildServerResources/editUrlBuildTrigger.jsp)
  ~
  ~ Original licence:
  ~
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
<%@ page import="com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>

<tr class="noBorder" >
    <td colspan="2">
        <em>Octopus machine added build trigger will add a build to the queue when a new machine is added.</em>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>">Octopus Url: <l:star/></label></th>
    <td>
      <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>" className="longField" onchange="window.octopusBuildTrigger.checkConnectivity();" />
      <span class="smallNote">
          e.g. https://example.org
      </span>
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>">API Key: <l:star/></label></th>
    <td>
       <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>" className="longField" onchange="window.octopusBuildTrigger.checkConnectivity();" />
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"></span>
    </td>
</tr>

<script type="text/javascript">
    var projectIdPropertyName = '<%=OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID%>';
    var octopusUrlPropertyName = '<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>';
    var octopusApiKeyPropertyName = '<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>';

    <jsp:include page="octopusBuildTrigger.js" />
</script>
