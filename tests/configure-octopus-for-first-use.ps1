$OctopusURI = "http://localhost:8081"
$octopusAdminUsername="Admin"
$octopusAdminPassword="Passw0rd123"

Add-Type -Path "${env:ProgramFiles}\Octopus Deploy\Octopus\Newtonsoft.Json.dll"
Add-Type -Path "${env:ProgramFiles}\Octopus Deploy\Octopus\Octopus.Client.dll"

Write-host "Signing into Octopus server at $OctopusURI"
#connect
$endpoint = new-object Octopus.Client.OctopusServerEndpoint $OctopusURI
$repository = new-object Octopus.Client.OctopusRepository $endpoint

#sign in
$credentials = New-Object Octopus.Client.Model.LoginCommand
$credentials.Username = $octopusAdminUsername
$credentials.Password = $octopusAdminPassword
$repository.Users.SignIn($credentials)

Write-Host "Creating a new api key"
$user = $repository.Users.GetCurrent()
$apiKey = $repository.Users.CreateApiKey($user, "TeamCity Octopus Build Trigger Testing")
[environment]::SetEnvironmentVariable("OctopusServerUrl", $OctopusURI, "User")
[environment]::SetEnvironmentVariable("OctopusServerUrl", $OctopusURI, "Machine")
[environment]::SetEnvironmentVariable("OctopusApiKey", $apiKey.ApiKey, "User")
[environment]::SetEnvironmentVariable("OctopusApiKey", $apiKey.ApiKey, "Machine")

Write-Host "Ensuring environments exist"
#create environments for the tentacles to go into
$environments = $repository.Environments.FindAll()
Write-host "Existing environments: [$($environments.Name -join ', ')]"
$environment = $environments | where-object { $_.Name -eq "Env1" }
if ($null -eq $environment) {
  Write-Host "Creating environment 'Env1'"
  $environment = New-Object Octopus.Client.Model.EnvironmentResource
  $environment.Name = "Env1"
  $repository.Environments.Create($environment) | Out-Null
} else {
  Write-Host "Environment 'Env1' already exists"
}
