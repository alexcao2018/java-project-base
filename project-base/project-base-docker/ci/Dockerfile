FROM openjdk:8-jdk-alpine

# ting yun
ENV ARTHAS_JAR_FILE @arthas_jar_file

# ting yun
ENV TINGYUN_JAR_FILE @tingyun_jar_file

# set time zone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

RUN apk add --no-cache tini
# Tini is now available at /sbin/tini
ENTRYPOINT ["/sbin/tini", "--"]

#  add file to /opt/app direcotry
ADD $ARTHAS_JAR_FILE /opt/app/
ADD $TINGYUN_JAR_FILE /opt/app/