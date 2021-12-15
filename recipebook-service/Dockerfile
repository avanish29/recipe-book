FROM adoptopenjdk:11-jre-hotspot
VOLUME /temp
EXPOSE 8080
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
ADD target/recipe-book.jar /app/recipe-book.jar
ENTRYPOINT [ "java", "-Dspring.profiles.active=prod", "-jar", "/app/recipe-book.jar" ]