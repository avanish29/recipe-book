# Recipe Book

Web application which allows users to manage your favourite recipes.

## Requirements

For building and running the application you need:

- [JDK 11.0.9.1](https://adoptopenjdk.net/)
- [Maven 3](https://maven.apache.org)
- [Postgres](https://www.postgresql.org/)

## Running the application locally

First build the project using :

```shell
mvn clean install
```
which will generate fat jar `recipebook-service-0.0.1-SNAPSHOT.jar` in `recipe-book\recipebook-service\target`, navigate to  `recipe-book\recipebook-service\target` folder and run :

```shell
java -Dspring.profiles.active=prod -jar recipe-book.jar
```
Once application start navigate to [Recipe Book Login Page](http://localhost:8080/) to access web application.

With docker compose run following command

```shell
docker-compose up
```

Once application start navigate to [Recipe Book Login Page](http://127.0.0.1:8080/) to access web application.

## Running the recipebook-service locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.recipebook.RecipebookServiceApplication` class from your IDE.

Alternatively you can navigate to `recipebook-service` and user the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Access Swagger documentation

To access swagger documentation go to [Recipe Book Swagger](http://localhost:8080/swagger-ui/index.html#/)
