FROM openjdk:8u212-jdk-alpine

RUN echo http://mirrors.aliyun.com/alpine/v3.9/main > /etc/apk/repositories
RUN echo http://mirrors.aliyun.com/alpine/v3.9/community >> /etc/apk/repositories

# arthas yun
ENV ARTHAS_JAR_FILE @arthas_jar_file

# ting yun
ENV TINGYUN_JAR_FILE @tingyun_jar_file

RUN apk add curl
RUN apk add busybox-extras
RUN apk add tcpdump
RUN apk add --no-cache tini
RUN apk add --no-cache fontconfig
# set time zone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

#  add file to /opt/app direcotry
ADD $ARTHAS_JAR_FILE /opt/app/
ADD $TINGYUN_JAR_FILE /opt/app/