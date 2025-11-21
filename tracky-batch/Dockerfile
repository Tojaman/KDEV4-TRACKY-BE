# OpenJDK 17 JRE를 베이스 이미지로 사용 (최적화 유지)
FROM eclipse-temurin:17-jre-jammy

ARG JAR_FILE=build/libs/*.jar
COPY /${JAR_FILE} /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "app.jar"]
