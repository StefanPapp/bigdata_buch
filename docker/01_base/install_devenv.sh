#!/bin/bash

apt-get update
apt-get install -y python3 python3-pip openjdk-8-jdk

cd /usr/lib/jvm
ln -s java-8-openjdk-amd64 jdk
