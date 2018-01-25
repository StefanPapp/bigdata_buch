#!/bin/sh
apt-get update
apt-get install -y wget openjdk-8-jdk
cd /usr/lib/jvm
ln -s java-8-openjdk-amd64 jdk
cd /usr/local
wget http://www-eu.apache.org/dist/kafka/0.11.0.1/kafka_2.12-0.11.0.1.tgz
tar -xvf kafka_2.12-0.11.0.1.tgz
mv kafka_2.12-0.11.0.1 kafka
