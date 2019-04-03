FROM mamohr/centos-java

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone \



ENV LANG=en_US.utf8
WORKDIR /home/chang
