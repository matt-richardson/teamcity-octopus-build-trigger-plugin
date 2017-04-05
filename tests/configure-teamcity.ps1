
set-location "c:\temp"
scriptcs configure-teamcity.csx
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
