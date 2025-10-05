FROM eclipse-temurin:11-jre

WORKDIR /app

COPY target/server-db-commands-1.0.0.jar /app/app.jar

ENV DB_HOST=db
ENV DB_PORT=3306
ENV DB_NAME=scholar
ENV DB_USER=scholar
ENV DB_PASSWORD=scholarpw
ENV DB_POOL_MAX=2

EXPOSE 7000

ENTRYPOINT ["java","-jar","/app/app.jar","com.innovationcenter.scholarapi.api.ApiMain"]
