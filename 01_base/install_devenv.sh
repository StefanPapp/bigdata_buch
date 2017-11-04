#!/bin/bash

apt-get update
apt-get install -y python python-pip openjdk-8-jdk

cd /usr/lib/jvm
ln -s java-8-openjdk-amd64 jdk
