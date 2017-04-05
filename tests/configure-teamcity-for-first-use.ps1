$progressPreference = 'silentlyContinue'

if ((Get-Service TeamCity).Status -eq "Stopped") {
    write-host "Starting the TeamCity service"
    Start-Service TeamCity
    write-host "Sleeping for 30 seconds"
    Sleep -Seconds 30
}

choco install googlechrome -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
choco install scriptcs -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
choco install chromedriver -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

set-location "c:\temp"
scriptcs -install Selenium.WebDriver
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

scriptcs configure-teamcity-for-first-use.csx
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
