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

$projects = @($repository.Projects.GetAll())
if ($projects.Length -gt 0)
{
  Write-Host "Project already exists - skipping create"
}
else
{
  $lifecycles = @($repository.Lifecycles.GetAll())
  $projectGroups = @($repository.ProjectGroups.GetAll())
  $project = new-object Octopus.Client.Model.ProjectResource
  $project.ProjectGroupId = $projectGroups[0].Id
  $project.LifecycleId = $lifecycles[0].Id
  $project.Name = "TestProject"
  $project = $repository.Projects.Create($project)

  $deploymentProcess = $repository.DeploymentProcesses.Get($project.DeploymentProcessId)
  $step = $deploymentProcess.AddOrUpdateStep("Run Script")
  $script = "write-host 'hello'"
  $scriptAction = new-object Octopus.Client.Model.DeploymentProcess.InlineScriptAction([Octopus.Client.Model.ScriptSyntax]::Powershell, $script)
  $scriptTarget = new-object Octopus.Client.Model.DeploymentProcess.ScriptTarget
  $step.AddOrUpdateScriptAction("Run Script", $scriptAction, [Octopus.Client.Model.DeploymentProcess.ScriptTarget]::Server)
  $step.Properties["Octopus.Action.TargetRoles"] = "app-server"
  $repository.DeploymentProcesses.Modify($deploymentProcess)
}
