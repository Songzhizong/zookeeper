FROM debian:12.1-slim
LABEL maintainer="cn.sh.ideal"
WORKDIR /app
ARG NATIVE_FILE
ADD ${NATIVE_FILE} /app/zookeeper
ADD conf /usr/
EXPOSE 2181 2888 3888
ENV JVM_OPTS=""
ENV SPRING_PROFILE="prd"
ENV FILE_ENCODING="UTF-8"
ENTRYPOINT ["sh","-c","exec ./zookeeper ./conf/zoo.cfg"]
