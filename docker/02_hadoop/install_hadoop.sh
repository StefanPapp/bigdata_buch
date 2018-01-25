#!/bin/sh

apt-get install -y openjdk-8-jdk openssh-server openssh-clients rsync wget
cd /usr/lib/jvm
ln -s java-8-openjdk-amd64 jdk
addgroup hadoop
useradd hduser -g hadoop -p hduser
su - hduser
ssh-keygen -t rsa -P ""
cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
cd /usr/local
wget http://www-eu.apache.org/dist/hadoop/common/hadoop-2.8.1/hadoop-2.8.1.tar.gz
tar -xvf hadoop-2.8.1.tar.gz
mv hadoop-2.8.1 hadoop
chown -R hduser:hadoop hadoop



