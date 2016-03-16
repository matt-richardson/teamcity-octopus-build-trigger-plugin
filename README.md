# teamcity-octopus-build-trigger-plugin

[![Build Status](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin.svg?branch=master)](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin)

A TeamCity plugin that polls Octopus Deploy, and triggers a TeamCity build when:
- [x] a deployment to an environment is complete
- [x] a successful deployment to an environment is complete
- [x] a release is created
- [x] a new tentacle is added

This is very much a work in progress, but feel free to give it a go, and let me know if you face any issues.
Constructive criticism received gratefully - this is my first real java project, and there's a lot I'm unaware of in that ecosystem.

# Installation

Download the [latest release](https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest), and drop it into your [<TeamCity Data Directory>](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory)/plugins folder. Restart TeamCity.

See [TeamCity documentation](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins) for more info.

# Outstanding items

- test older versions of Octopus
- pass details of trigger item (ie, release name), to build. At the moment, you need to parse a configuration parameter `teamcity.build.triggeredBy`, which is designed to be a human readable rather than machine readable.
- improve logging
- show a message when an update is available

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

If you want to disable analytics, you can set the `octopus.build.trigger.analytics.enabled` [internal
property](https://confluence.jetbrains.com/display/TCD9/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalpropertiesinternal.properties)
to `false`.
