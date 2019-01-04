$progressPreference = 'silentlyContinue'

write-host "Installing teamcity server"
& choco install teamcity -y
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

write-host "Expanding teamcity configuration git repository"
if (-not (Test-Path c:\TeamCityConfiguration))
{
  Expand-Archive c:\TeamCityConfiguration.zip -DestinationPath c:\

  #remove rubbish that mac has left behind
  Get-ChildItem 'C:\TeamCityConfiguration' -filter '.DS_Store' -Recurse | Remove-item
  Remove-Item 'C:\__MACOSX' -Recurse -Force
}
