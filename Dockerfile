# Java 17 버전을 씁니다 (현재 사용 중인 버전에 맞게)
FROM eclipse-temurin:17-jdk-alpine

# 빌드된 jar 파일 경로
ARG JAR_FILE=build/libs/*.jar

# jar 파일을 컨테이너 내부의 app.jar로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 켜질 때 실행할 명령어
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]