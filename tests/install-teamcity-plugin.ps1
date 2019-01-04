
function Test-ShouldCopyFile {
  if (-not (Test-Path $destination)) {
    return $true
  }

  return ((Get-FileHash $source).Hash -ne (Get-FileHash $destination).Hash)
}

write-host "Installing plugin"
$destination = "C:\ProgramData\JetBrains\TeamCity\plugins\octopus-build-trigger.zip"
$source = "c:\octopus-build-trigger.zip"

if (Test-ShouldCopyFile) {
  Copy-Item $source $destination
  Restart-Service "TeamCity"
}
else {
  write-host "Skipping download - file already exists"
}
