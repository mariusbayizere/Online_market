# Stage 1: Build the application
FROM ubuntu:24.04 AS build

# Set the working directory
WORKDIR /app

# Install necessary dependencies for Java and Maven
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    maven \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Copy the pom.xml and install dependencies
COPY pom.xml .

# Download dependencies (this helps to cache dependencies to avoid redownloading every time)
RUN mvn dependency:go-offline

# Copy the entire source code
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Install required dependencies for PostgreSQL JDBC driver
RUN apt-get update && \
    apt-get install -y \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/Project_Online_market-0.0.1-SNAPSHOT.jar app.jar

# Create volume for persistent data
VOLUME /app/data

# Expose the port your Spring Boot app will run on
EXPOSE 8080

# Command to run the jar file with explicit environment variable for database URL
ENTRYPOINT ["java", "-Dspring.datasource.url=jdbc:postgresql://db:5432/online_market", "-jar", "app.jar"]