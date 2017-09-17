#!/bin/sh

cd ~
apk --update add git zsh wget shadow
sh -c "$(wget https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh -O -)"
git clone https://github.com/StefanPapp/zshconf_linux
git clone https://github.com/rupa/z.git
rm .zshrc
ln -s zshconf_linux/.zshrc .zshrc
chsh -s /bin/zsh root
touch /root/.z


