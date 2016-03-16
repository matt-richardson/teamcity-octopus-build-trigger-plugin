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
      <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>" className="longField" onchange="window.octopusBuildTrigger.connectivityChecker.check();" />
      <span class="smallNote">
          e.g. https://example.org
      </span>
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>">API Key: <l:star/></label></th>
    <td>
       <props:textProperty name="<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>" className="longField" onchange="window.octopusBuildTrigger.connectivityChecker.check();" />
       <span class="error" id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"></span>
    </td>
</tr>

<script type="text/javascript">

window.octopusBuildTrigger = function() {
  var connectivityChecker = function() {
    var url;
    var apiKey;

    function clearError() {
      $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"]').text("");
      $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"]').text("");
    }

    function setError(urlError, apiKeyError) {
      if (urlError) {
        $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"]').text(urlError);
        $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"]').text("");
      }
      else {
        $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"]').text("");
        $j('[id="error_<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"]').text(apiKeyError);
      }
    }

    function handleNetworkFailureResponse(xhr, response) {
      setError("Unable to connect. Please ensure the url and apikey are correct.", null);
    }

    function handle401Response(xhr, response) {
      setError(null, "Invalid api key");
    }

    function handle404Response(xhr, response) {
      setError("Unable to connect to url: 404 Not Found", null);
    }

    function handleFailureResponse(xhr, response) {
      debugger;
      setError("Unable to connect to url: unknown failure", null);
    }

    function handleExceptionResponse(response, err) {
      debugger;
      setError("Unable to connect to url: exception", null);
    }

    function check() {
      url = $$('[name="prop:<%=OctopusBuildTriggerUtil.OCTOPUS_URL%>"]')[0].value.replace(/\/$/, '');
      apiKey = $$('[name="prop:<%=OctopusBuildTriggerUtil.OCTOPUS_APIKEY%>"]')[0].value;

      new Ajax.Request(url + '/api?apikey=' + apiKey, {
          requestHeaders: {
              'X-Prototype-Version': null,
              'X-Requested-With': null
          },
          onSuccess: clearError,
          on0: handleNetworkFailureResponse,
          on303: handle401Response,
          on401: handle401Response,
          on404: handle404Response,
          onFailure: handleFailureResponse,
          onException: handleExceptionResponse,
          method: 'GET'
      });
    }
    return {
      check : check
    }
  }();

  return {
    connectivityChecker : connectivityChecker
  }
}();

$j(document).ready(function() {
  window.octopusBuildTrigger.connectivityChecker.check();
});
</script>

