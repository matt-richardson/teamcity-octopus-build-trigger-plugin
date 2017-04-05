Configuration OctopusServer
{
    Import-DscResource -ModuleName OctopusDSC

    Node "localhost"
    {
        LocalConfigurationManager
        {
            DebugMode = "ForceModuleImport"
        }

        cOctopusServer OctopusServer
        {
            Ensure = "Present"
            State = "Started"
            Name = "OctopusServer"
            WebListenPrefix = "http://localhost:8081"
            SqlDbConnectionString = "Server=(local)\SQLEXPRESS;Database=Octopus;Trusted_Connection=True;"
            OctopusAdminUsername = "admin"
            OctopusAdminPassword = "Passw0rd123"
            AllowCollectionOfAnonymousUsageStatistics = $false
        }

        cOctopusServerUsernamePasswordAuthentication "Enable Username/Password Auth"
        {
            InstanceName = "OctopusServer"
            Enabled = $true
        }
    }
}
