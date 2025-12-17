# Step 1: Build the application using a modern Maven image
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Use a supported Eclipse Temurin image for the final package
FROM eclipse-temurin:17-jdk-jammy
# This line finds your JAR file regardless of the name
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
