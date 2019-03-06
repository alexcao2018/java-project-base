FROM openjdk:8-jdk-alpine

# ting yun
ENV TINGYUN_JAR_FILE @tingyun_jar_file

# set time zone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

#  add file to /opt/app direcotry
ADD $TINGYUN_JAR_FILE /opt/app/