FROM openjdk:8u212-jdk-alpine

# change apk mirror
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# ting yun
ARG TINGYUN_JAR_FILE="@tingyun_jar_file"

# sky walking
ARG SKYWALKING_AGENT_DIR="@skywalking_agent_dir"

# arthas
ARG ARTHAS_DIR="@arthas_dir"

RUN apk add curl
RUN apk add busybox-extras
RUN apk add tcpdump
RUN apk add --no-cache tini
# set time zone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

#  add file to /opt/app direcotry
ADD ${ARTHAS_DIR} /opt/app/${ARTHAS_DIR}
ADD ${TINGYUN_JAR_FILE} /opt/app/
ADD ${SKYWALKING_AGENT_DIR} /opt/app/${SKYWALKING_AGENT_DIR}
