FROM openjdk:17-oraclelinux8

# 이미 만들어진 JAR 파일을 복사
COPY build/libs/*.jar /app/recipe.jar
# 필요한 만큼 모든 모듈에 대해 위와 같이 복사할 수 있습니다.

# 실행
ENTRYPOINT ["java", "-jar"]
CMD ["/app/recipe.jar"]