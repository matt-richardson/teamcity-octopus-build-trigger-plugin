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

check_plugin_installed "vagrant-dsc"
check_plugin_installed "vagrant-winrm"
check_plugin_installed "vagrant-winrm-syncedfolders"

echo "Running 'vagrant up --provider virtualbox'"
time vagrant up --provider virtualbox # --debug &> vagrant.log

echo "Dont forget to run 'vagrant destroy -f' when you have finished"