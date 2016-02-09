# teamcity-octopus-build-trigger-plugin

[![Build Status](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin.svg?branch=master)](https://travis-ci.org/matt-richardson/teamcity-octopus-build-trigger-plugin)

A TeamCity plugin that polls Octopus Deploy, and triggers a TeamCity build when:
[x] a deployment to an environment is complete
[x] a successful deployment to an environment is complete
[ ] a release is created
[ ] a new tentacle is added

This is very much a work in progress, but feel free to give it a go, and let me know if you face any issues.

# Installation

Download the [latest release](https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest), and drop it into your [<TeamCity Data Directory>](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory)/plugins folder ([See docs for more info](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins). Restart TeamCity.