FROM ubuntu

ADD base_install.sh /tmp/base_install.sh
RUN chmod a+x /tmp/base_install.sh &&  /tmp/base_install.sh 

ADD install_devenv.sh /tmp/install_devenv.sh
RUN chmod a+x /tmp/install_devenv.sh &&  /tmp/install_devenv.sh 

ADD install_zsh.sh /tmp/install_zsh.sh
RUN chmod a+x /tmp/install_zsh.sh &&  /tmp/install_zsh.sh 

ADD install_awevim.sh /tmp/install_awevim.sh
RUN chmod a+x /tmp/install_awevim.sh &&  /tmp/install_awevim.sh 

ADD zshrc_tmp /tmp/zshrc_tmp
RUN mv /tmp/zshrc_tmp  ~/.zshrc

#ADD vim_tmp /tmp/vim_tmp
#RUN mv /tmp/vim_tmp  ~/.vimrc