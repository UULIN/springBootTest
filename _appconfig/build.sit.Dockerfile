FROM registry.qunhequnhe.com/public/maven:3.5.2 as builder
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -U -DskipTests

FROM registry.qunhequnhe.com/infra/centos7-jdk8:0.2.2
RUN mkdir /opt/spring-boot
COPY --from=builder /usr/src/app/target/counter-slow.jar /opt/spring-boot/root.jar

ENV LANG en_US.utf8
ENV TZ "Asia/Shanghai"
ENV JAVA_OPTIONS=${JAVA_OPTIONS}

WORKDIR /opt/spring-boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTIONS -Djava.security.egd=file:/dev/./urandom -jar root.jar"]
