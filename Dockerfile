FROM openjdk:8-jre-alpine
COPY build/libs/taskbank-server-0.0.1.jar /opt/spring/
WORKDIR /opt/spring
EXPOSE 8080
CMD ["java", "-jar", "taskbank-server-0.0.1.jar"]