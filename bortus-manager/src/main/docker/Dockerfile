FROM java:8
ENV TZ Asia/Shanghai
VOLUME /tmp
COPY bortus-manager-0.1.jar app.jar
ENTRYPOINT ["java","-jar", "-Xmx512m", "-XX:MaxMetaspaceSize=128m", "-XX:CompressedClassSpaceSize=24m","/app.jar"]