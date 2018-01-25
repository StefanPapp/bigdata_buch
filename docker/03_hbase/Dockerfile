FROM base

EXPOSE 16010

ADD hbase-site.xml /tmp/hbase-site.xml
ADD hbase-env.sh /tmp/hbase-env.sh

ADD install_hbase.sh /tmp/install_hbase.sh 
RUN chmod a+x /tmp/install_hbase.sh  &&  /tmp/install_hbase.sh 