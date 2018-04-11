#!/bin/bash

wget https://repo.continuum.io/miniconda/Miniconda2-latest-Linux-x86_64.sh -O ~/miniconda.sh
bash ~/miniconda.sh -b -p $HOME/miniconda

echo "" >> ~/.zshrc
echo "export PATH=$HOME/miniconda/bin:$PATH" >> ~/.zshrc

echo "" >> ~/.bashrc
echo "export PATH=$HOME/miniconda/bin:$PATH" >> ~/.bashrc

export PATH="$HOME/miniconda/bin:\$PATH"

conda install jupyter
#jupyter notebook --ip=127.0.0.1 --allow-root
