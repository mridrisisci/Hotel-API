# Start with Amazon Corretto 17 Alpine base image
FROM amazoncorretto:22-alpine

# Install curl on Alpine
RUN apk update && apk add --no-cache curl

# Copy the jar file into the image
COPY target/app.jar /app.jar

# Ensure the configuration file is in the correct location
COPY src/main/resources/hibernate.cfg.xml /app/config/hibernate.cfg.xml

# Expose the port your app runs on
EXPOSE 7000

# Command to run your app
CMD ["java", "-jar", "/app.jar"]