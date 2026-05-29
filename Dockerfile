# Multi-stage build for Maven and OpenJDK 25
FROM eclipse-temurin:25-jdk AS builder

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build shaded JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre

# Install Xvfb, window manager, VNC, noVNC, and websockify
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    xvfb \
    x11vnc \
    openbox \
    novnc \
    websockify \
    dbus-x11 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy compiled JAR and SQLite database
COPY --from=builder /app/target/academic-report-system-1.0-SNAPSHOT.jar /app/academic-report-system.jar
COPY academic_report.db /app/academic_report.db

# Ensure index.html is overwritten safely (removing existing symlink if any)
RUN rm -f /usr/share/novnc/index.html
COPY Index.html /usr/share/novnc/index.html
COPY Index.html /usr/share/novnc/Index.html

# Copy and prepare startup script
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# Expose port (Render dynamic PORT)
EXPOSE 10000

ENTRYPOINT ["/app/start.sh"]
