$progressPreference = 'silentlyContinue'

write-host "Installing teamcity server"
& choco install teamcity -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

write-host "Expanding teamcity configuration git repository"
Expand-Archive c:\TeamCityConfiguration.zip -DestinationPath c:\TeamCityConfiguration
