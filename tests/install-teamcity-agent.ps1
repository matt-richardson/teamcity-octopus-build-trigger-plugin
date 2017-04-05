write-host "Installing teamcity agent"
& choco install teamcityagent -y --allow-empty-checksums -params 'serverUrl=http://localhost:8111 agentName=local agentDir=c:\\teamcity\\agent'
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# wait until its registered

write-output "Looping until agent started"
while (-not ((get-content "c:\teamcity\agent\logs\teamcity-agent.log" -raw) -like "*Agent Web server started*")) {
    write-output "$(date) Waiting until agent started"
    start-sleep 5
}

write-output "Looping until agent starts upgrade"
while (-not ((get-content "c:\teamcity\agent\logs\teamcity-agent.log" -raw) -like "*Agent exited. Upgrade process*")) {
    write-output "$(date) Waiting until agent exits for upgrade"
    start-sleep 5
}

write-output "Looping until agent connects to server"
while (-not ((get-content "c:\teamcity\agent\logs\teamcity-agent.log" -raw) -like "*Updating agent parameters on the server*")) {
    write-output "$(date) Waiting until agent re-connects to server"
    start-sleep 5
}
