FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN ./mvnw package --no-daemon

FROM adoptopenjdk:21-jdk-hotspot

EXPOSE 8080

COPY --from=build /target/demo-1.jar app.jar

ENTRYPOINT java -jar app.jar
