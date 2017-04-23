
$destination = "C:\ProgramData\JetBrains\TeamCity\plugins\octopus-build-trigger.zip"

write-host "Downloading plugin from github"
if (-not (Test-Path $destination)) {
  $url = "https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/download/2.5.1%2Bbuild.143/octopus-build-trigger.zip"
  Invoke-webrequest $url -outfile $destination

  Restart-Service "TeamCity"
}
else {
  write-host "Skipping download - file already exists"
}
