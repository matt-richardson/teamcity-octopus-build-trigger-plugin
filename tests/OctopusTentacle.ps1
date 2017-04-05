Configuration OctopusTentacle
{
    Import-DscResource -ModuleName OctopusDSC

    Node "localhost"
    {
        LocalConfigurationManager
        {
            DebugMode = "ForceModuleImport"
        }

        cTentacleAgent OctopusTentacle
        {
            Ensure = "Present"
            State = "Started"
            Name = "Tentacle"
            ApiKey = $Env:OctopusApiKey
            OctopusServerUrl = "http://localhost:8081"
            Environments = "Env1"
            Roles = "app-server"
            CommunicationMode = "Listen"
            ListenPort = 10933
            DefaultApplicationDirectory = "C:\Applications"
            TentacleHomeDirectory = "C:\Octopus"
            PublicHostNameConfiguration = "Custom"
            CustomPublicHostName = "localhost"
        }
    }
}
