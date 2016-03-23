# teamcity-octopus-build-trigger-plugin

[![Build Status](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin.svg?branch=master)](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin)

A TeamCity plugin that polls Octopus Deploy, and triggers a TeamCity build when:
- [x] a deployment to an environment is complete
- [x] a successful deployment to an environment is complete
- [x] a release is created
- [x] a new tentacle is added

# Installation

Download the [latest release](https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest), and drop it into your [<TeamCity Data Directory>](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory)/plugins folder. Restart TeamCity.

See [TeamCity documentation](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins) for more info.

# CORS (Cross Origin Resource Sharing)

You may run into an issue where you get 'Unable to connect. Please ensure the url and API key are correct.' errors when
configuring the build triggers, even though the URL and API key are correct. If this is the case, please ensure you [configure CORS](http://help.octopusdeploy.com/discussions/problems/42234-access-control-allow-origin-headers-not-being-sent-with-options-responses-from-api-321#comment_38476446)
on the Octopus Deploy server.

You will most likely need to execute `Octopus.Server.exe configure --webCorsWhitelist=teamcity.example.com` on your
Octopus Deploy server (changing teamcity.example.com for your TeamCity server's URL).

# Outstanding items

- test older versions of Octopus
- improve logging
- show a message when an update is available
- add mutation testing using something like [Jester](http://jester.sourceforge.net/)

# Logging

If you want to turn on logging, add the following to `<TeamCity server home>\conf\teamcity-server-log4j.xml`:

```xml
<appender name="OCTOPUS_DEPLOY.LOG" class="jetbrains.buildServer.util.TCRollingFileAppender">
  <param name="file" value="${teamcity_logs}teamcity-octopusDeploy.log" />
  <param name="maxBackupIndex" value="3" />
  <layout class="org.apache.log4j.PatternLayout">
    <param name="ConversionPattern" value="[%d] %6p - %c - %m %n" />
  </layout>
</appender>
<category name="com.mjrichardson.teamCity.buildTriggers" additivity="false">
  <priority value="DEBUG" />
  <appender-ref ref="OCTOPUS_DEPLOY.LOG" />
</category>
 ```

# Analytics

This project uses [Google Analytics](https://www.google.co.uk/analytics/) to track feature usage and exceptions.
This information is used only to understand real world usage and guide future improvements.

Only basic feature usage information is tracked, with no personally identifiable information shared.
Any exceptions are masked to remove sensitive data.

Specifically, these events are tracked:
* Exceptions (The top level exception message (without the stack tracce) with IP address and urls masked)
* When a new trigger is added
* When a new build is triggered
* When the DeploymentProvider needs to fallback to using the `/api/Deployments` endpoint when the `/api/progession`
endpoint does not return enough information
* Whether the result of the fallback returned the same, better or worse information

The information tracked includes:
* TeamCity version
* Plugin version
* A random 'sessions id', which changes every time the TeamCity server is restarted
* The `user.region` or `user.country` java system properties (if set)
* The `user.language` java system property
* The `user.language` java system property
* The `file.encoding` java system property

If you want to disable analytics, you can set the `octopus.build.trigger.analytics.enabled` [internal
property](https://confluence.jetbrains.com/display/TCD9/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalpropertiesinternal.properties)
to `false`. This requires a restart.

# Feedback

Constructive criticism gratefully received. This is my first real java project, so while this project has taught me
a lot, there's still a whole lot more I'm unaware of in that ecosystem.

# Acknowledgements

Grateful thanks to JetBrains, especially Yegor Yarko for their assistance with helping me while I was learning.

Also thanks for providing the [UrlBuildTrigger](http://svn.jetbrains.org/teamcity/plugins/url-build-trigger) plugin
as a sample, which was the basis of this plugin.

# License

This project is licensed under the Apache 2.0 license.

Portions of this code are modified from the [UrlBuildTrigger](http://svn.jetbrains.org/teamcity/plugins/url-build-trigger)
plugin, which is Copyright (c) 2000-2013 JetBrains s.r.o., and released under the Apache 2.0 license as well.
