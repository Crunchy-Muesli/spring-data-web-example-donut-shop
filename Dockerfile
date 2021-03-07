FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=donut-shop-server/target/*.jar
COPY ${JAR_FILE} app.jar
ADD donut-shop-server/src/main/resources/application.properties.containerized /app/application.properties
ADD donut-shop-server/flyway/V1__create.sql /app/flyway/V1__create.sql
ADD donut-shop-server/flyway/V2__update.sql /app/flyway/V2__update.sql
ENTRYPOINT ["java","-jar","-Dspring.config.location=classpath:file:/app/application-properties","/app.jar"]
