# teamcity-octopus-build-trigger-plugin

[![Build Status](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin.svg?branch=master)](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin)

A TeamCity plugin that polls Octopus Deploy, and triggers a TeamCity build when:
- [x] a deployment to an environment is complete
- [x] a successful deployment to an environment is complete
- [x] a release is created
- [x] a new tentacle is added
- [x] the release process for a project is changed

# Installation

Download the [latest release](https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest), and drop it into your [<TeamCity Data Directory>](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory)/plugins folder. Restart TeamCity.

See [TeamCity documentation](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins) for more info.

# Usage

Once the plugin is installed, add one of the triggers to your build. All 3 look similar to:

![TeamCity trigger configuration](teamcity-trigger-configuration.png "TeamCity trigger configuration")

Once a build is triggered, parameters will be passed to the build:

![TeamCity build parameters](teamcity-build-params.png "TeamCity build parameters")

# Outstanding items

- test older versions of Octopus
- improve logging
- add mutation testing using something like [Jester](http://jester.sourceforge.net/)

# Logging

If you want to turn on logging, add the following to `<TeamCity server home>\conf\teamcity-server-log4j.xml`:

```xml
<appender name="TEAMCITY_OCTOPUS_DEPLOY_BUILD_TRIGGER_PLUGIN.LOG" class="jetbrains.buildServer.util.TCRollingFileAppender">
  <param name="file" value="${teamcity_logs}teamcity-octopus-deploy-build-trigger-plugin.log" />
  <param name="maxBackupIndex" value="3" />
  <layout class="org.apache.log4j.PatternLayout">
    <param name="ConversionPattern" value="[%d] %6p - %c - %m %n" />
  </layout>
</appender>
<category name="com.mjrichardson.teamCity.buildTriggers" additivity="false">
  <priority value="DEBUG" />
  <appender-ref ref="TEAMCITY_OCTOPUS_DEPLOY_BUILD_TRIGGER_PLUGIN.LOG" />
</category>
 ```

# Update checks

A check is made once every 24 hours to see if there is a new version available, and if so, shows a banner
on the trigger dialog suggestion an upgrade.

If you want to disable update checking, you can set the `octopus.build.trigger.update.check.enabled` [internal
property](https://confluence.jetbrains.com/display/TCD9/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalpropertiesinternal.properties)
to `false`. This does not require a restart.


# Metrics

Metrics are exposed as a json endpoint at https://your-teamcity-server/octopus-build-trigger/metrics.html, allowing
you to track and monitor internal stats such as cache hits & misses, outbound calls, bresponse time and more.

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
to `false`. This does not require a restart.

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
