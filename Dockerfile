FROM openjdk:8-jdk-alpine as BUILD

ENV GRADLE_USER_HOME /etc/app/.gradle

WORKDIR /etc/app

# copy files required to pre-cache dependencies
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .

RUN ./gradlew preloadDependencies

# copy files required to build project
COPY config/ config/
COPY src/ src/

RUN ./gradlew build

FROM openjdk:8-jre-alpine as RUN

WORKDIR /etc/app

COPY --from=BUILD /etc/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
