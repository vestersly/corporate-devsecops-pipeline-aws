FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/webapp-1.0.0.jar /app/app.jar
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
