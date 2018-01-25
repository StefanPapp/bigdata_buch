#!/bin/sh
apt-get update
apt-get install -y openjdk-8-jdk openssh-server openssh-clients rsync wget vim
cd /usr/lib/jvm
ln -s java-8-openjdk-amd64 jdk
addgroup hadoop
useradd hduser -g hadoop -p hduser
su - hduser
ssh-keygen -t rsa -P ""
cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
cd /usr/local
wget http://www-eu.apache.org/dist/hbase/1.2.6/hbase-1.2.6-bin.tar.gz  
tar -xvf hbase-1.2.6-bin.tar.gz
mv hbase-1.2.6 hbase
chown -R hduser:hadoop hbase

mkdir /usr/local/hbase/data
mkdir /usr/local/hbase/zookeeper
mkdir /usr/local/hbase/logs

chown hduser:hadoop /usr/local/hbase/data
chown hduser:hadoop /usr/local/hbase/zookeeper
chown hduser:hadoop /usr/local/hbase/logs

cp -rf /tmp/hbase-site.xml /usr/local/hbase/conf/hbase-site.xml
cp -rf /tmp/hbase-env.sh /usr/local/hbase/conf/hbase-env.sh 

