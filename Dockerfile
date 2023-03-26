FROM openjdk:11
EXPOSE 8080
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} app.jar
COPY /src/main/resources/prices /src/main/resources/prices
COPY /src/test/resources/prices /src/test/resources/prices
ENTRYPOINT ["java","-jar","/app.jar"]