FROM openjdk:17
ARG SERVER_PORT
VOLUME /tmp
EXPOSE $SERVER_PORT
ARG JAR_FILE=build/libs/storage-service-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]