# Multi-stage Dockerfile
#Stage 1 Jar
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code from backend
COPY backend/src backend/src

#Build the jar accomodatting for linux windows weird crossovers
RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew \
    && ./gradlew bootJar --no-daemon \
    && find build/libs/ -name "*.jar" \
       ! -name "*-plain.jar" \
       -exec cp {} /app/app.jar \;

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built fat JAR from the build stage
COPY --from=build /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]