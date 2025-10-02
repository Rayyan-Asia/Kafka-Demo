FROM openjdk:17-jdk-slim

LABEL maintainer="RayyanTawfieg@gmail.com"

COPY ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]