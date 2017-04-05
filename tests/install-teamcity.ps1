$progressPreference = 'silentlyContinue'

write-host "Installing teamcity server"
& choco install teamcity -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
