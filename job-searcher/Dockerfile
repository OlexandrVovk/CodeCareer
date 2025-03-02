FROM openjdk:20
WORKDIR /app
COPY /target/job-searcher-0.0.1-SNAPSHOT.jar job-searcher.jar
ENTRYPOINT ["java", "-jar", "./job-searcher.jar"]