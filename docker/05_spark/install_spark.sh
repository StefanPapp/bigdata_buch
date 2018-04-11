#!/bin/bash
apt-get update

cd /usr/local
wget http://www-us.apache.org/dist/spark/spark-2.2.0/spark-2.2.0-bin-hadoop2.7.tgz
tar -xvf spark-2.2.0-bin-hadoop2.7.tgz
mv spark-2.2.0-bin-hadoop2.7 spark

echo "" >> ~/.zshrc
echo "export PATH=\$PATH:/usr/local/spark/bin" >> ~/.zshrc
