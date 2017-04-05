#!/bin/bash

function check_plugin_installed() {
    param=$1

    vagrant plugin list | grep -i "$param " > /dev/null

    if [ $? == 0 ]; then
      echo "Vagrant plugin $param exists - good."
    else
      echo "Vagrant plugin $param not installed."
      vagrant plugin install $param
    fi
}

function check_env_var() {
    param=$1
    if [ -z "${!param}" ]; then
        echo "Please set the $param environment variable"
        exit 1
    fi
}

which vagrant > /dev/null
if [ $? != 0 ]; then
  echo "Please install vagrant from vagrantup.com."
  exit 1
fi
echo "Vagrant installed - good."

which VBoxManage > /dev/null
if [ $? != 0 ]; then
  echo "Please install VirtualBox from virtualbox.org."
  exit 1
fi
echo "VirtualBox installed - good."

POWERSHELL_INSTALLED=0
which powershell > /dev/null
if [ $? != 0 ]; then
  POWERSHELL_INSTALLED=1
  echo "Powershell does not appear to be installed. To install, see https://github.com/PowerShell/PowerShell/blob/master/docs/installation/linux.md."
else
  echo "Powershell installed - good."
fi

check_plugin_installed "vagrant-dsc"
check_plugin_installed "vagrant-winrm"
check_plugin_installed "vagrant-winrm-syncedfolders"

if [ $POWERSHELL_INSTALLED == 0 ]; then
  echo "Running PSScriptAnalyzer"
read -r -d '' SCRIPT << EOM
Import-Module PSScriptAnalyzer
\$excludedRules = @('PSUseShouldProcessForStateChangingFunctions', 'PSAvoidUsingPlainTextForPassword', 'PSAvoidUsingUserNameAndPassWordParams', 'PSAvoidUsingConvertToSecureStringWithPlainText')
\$results = Invoke-ScriptAnalyzer ./OctopusDSC/DSCResources -recurse -exclude \$excludedRules
write-output \$results
write-output "PSScriptAnalyzer found \$(\$results.length) issues"
exit \$results.length
EOM
  powershell -command "$SCRIPT"
  if [ $? != 0 ]; then
    echo "Aborting as PSScriptAnalyzer found issues."
    exit 1
  fi

  echo "Running Pester Tests"
  powershell -command "Invoke-Pester -OutputFile PesterTestResults.xml -OutputFormat NUnitXml -EnableExit"
  if [ $? != 0 ]; then
    echo "Pester tests failed."
    exit 1
  fi
fi

echo "Running 'vagrant up --provider virtualbox'"
time vagrant up --provider virtualbox # --debug &> vagrant.log

echo "Dont forget to run 'vagrant destroy -f' when you have finished"