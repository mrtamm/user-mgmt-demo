# ---------- Build stage ----------
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS build

WORKDIR /app

# Copy Gradle configuration first to take advantage of Docker layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy application source
COPY src src

# Build the executable JAR
RUN ./gradlew bootJar --no-daemon


# ---------- Runtime stage ----------
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

COPY --from=build /app/build/libs/user-mgmt-demo.jar user-mgmt-demo.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "user-mgmt-demo.jar"]