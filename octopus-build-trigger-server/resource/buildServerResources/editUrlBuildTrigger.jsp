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
<%@ page import="jetbrains.buildServer.buildTriggers.url.UrlBuildTriggerUtil" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>

<tr class="noBorder" >
    <td colspan="2">
        <em>URL build trigger will add a build to the queue when changes are detected at the specified URL.</em>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=UrlBuildTriggerUtil.URL_PARAM%>">URL: <l:star/></label></th>
    <td>
       <props:textProperty name="<%=UrlBuildTriggerUtil.URL_PARAM%>" className="longField"/>
      <span class="smallNote">
          e.g. http://svn.jetbrains.org/teamcity/plugins/octopus-build-trigger/trunk/build.xml,<br/>
          ftp://admin:admin@172.168.0.2:2121/dir/artifact.zip. <br/>
      </span>
        <span class="error" id="error_<%=UrlBuildTriggerUtil.URL_PARAM%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=UrlBuildTriggerUtil.USERNAME_PARAM%>">Username: </label></th>
    <td>
       <props:textProperty name="<%=UrlBuildTriggerUtil.USERNAME_PARAM%>"/>
      <span class="smallNote">
          Used for HTTP basic authentication and FTP login
      </span>
        <span class="error" id="error_<%=UrlBuildTriggerUtil.USERNAME_PARAM%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th><label for="<%=UrlBuildTriggerUtil.PASSWORD_PARAM%>">Password: </label></th>
    <td>
       <props:passwordProperty name="<%=UrlBuildTriggerUtil.PASSWORD_PARAM%>"/>
      <span class="smallNote">
          Used for HTTP basic authentication and FTP login
      </span>
        <span class="error" id="error_<%=UrlBuildTriggerUtil.PASSWORD_PARAM%>"></span>
    </td>
</tr>