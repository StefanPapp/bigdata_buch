#!/bin/bash

#sh -c "$(wget http://archive.cloudera.com/kudu/ubuntu/xenial/amd64/kudu/cloudera.list /etc/apt/sources.list.d/cloudera.list)"

wget -O /etc/apt/sources.list.d/cloudera.list  http://archive.cloudera.com/kudu/ubuntu/xenial/amd64/kudu/cloudera.list 
apt-get update

apt-get install -y --allow-unauthenticated kudu                     # Base Kudu files
apt-get install -y --allow-unauthenticated kudu-master              # Service scripts for managing kudu-master
apt-get install -y --allow-unauthenticated kudu-tserver             # Service scripts for managing kudu-tserver
apt-get install -y --allow-unauthenticated libkuduclient0           # Kudu C++ client shared library
apt-get install -y --allow-unauthenticated libkuduclient-dev        # Kudu C++ client SDK