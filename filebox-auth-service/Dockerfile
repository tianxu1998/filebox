FROM openjdk:8-jre
ARG JAR_FILE
ADD target/$JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=GMT+08", "/app.jar"]