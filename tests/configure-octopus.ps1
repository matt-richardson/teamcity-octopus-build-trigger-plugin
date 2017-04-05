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

# create TestProject
