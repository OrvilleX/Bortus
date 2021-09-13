FROM java:8

ENV TZ Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

VOLUME /tmp
COPY bortus-manager-0.1.jar app.jar
ENTRYPOINT ["java","-jar", "-Xmx512m", "-Duser.timezone=GMT+8", "-XX:MaxMetaspaceSize=128m", "-XX:CompressedClassSpaceSize=24m","/app.jar"]
