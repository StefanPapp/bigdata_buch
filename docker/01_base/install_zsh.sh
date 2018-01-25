#!/bin/bash

apt-get update
apt-get install -y zsh

cd ~

sh -c "$(wget https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh -O -)"

# Make ZSH default shell
chsh -s /bin/zsh

#git clone https://github.com/StefanPapp/zshconf_linux
git clone https://github.com/rupa/z.git
#rm .zshrc
#ln -s zshconf_linux/.zshrc .zshrc
touch /root/.z

#zsh syntax highlighting
git clone https://github.com/zsh-users/zsh-syntax-highlighting.git


#install theme
git clone https://github.com/sobolevn/sobole-zsh-theme.git
mkdir -p /root/.oh-my-zsh/custom/themes/
ln -s $PWD/sobole-zsh-theme/sobole.zsh-theme ~/.oh-my-zsh/custom/themes/sobole.zsh-theme

#pip install --user powerline-status
#wget https://github.com/powerline/powerline/raw/develop/font/PowerlineSymbols.otf
#wget https://github.com/powerline/powerline/raw/develop/font/10-powerline-symbols.conf
#mv PowerlineSymbols.otf ~/.local/share/fonts/
#fc-cache -vf ~/.local/share/fonts/
#mv 10-powerline-symbols.conf ~/.config/fontconfig/conf.d/
