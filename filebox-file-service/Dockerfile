FROM openjdk:8-jre
ARG JAR_FILE
ADD target/$JAR_FILE /app.jar
#暴露调试端口，容器内部
EXPOSE 60006
ENTRYPOINT ["java", "-jar","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=60006" , "-Duser.timezone=GMT+08", "/app.jar"]