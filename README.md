# Donut shop

Simple exercise project, a backend for an imaginary donut shop. 

It uses spring-web to expose the rest services, spring-data-jpa to save donuts and ingredients, spring-data-mongodb for orders and reviews, spring-security to protect the admin rest service with basic-auth.

# Requirements

- JDK 11+
- Maven 3.6.3+
- Docker
- Docker Compose
  
### Without Docker/Docker Compose
- Postgresql 10.4
- MongoDB 4.4.4

# Usage

After a `mvn clean install` the application can be started using 

```
mvn spring-boot:run -pl donut-shop-server
```

in the main directory of the project.
But since the environment is missing, the build should probably fail. 
If instances of Postgresql and MongoDB are already available then this command is all that is needed to run the application, otherwise keep reading.

### Docker Environment 

In the `DockerEnvOnly` folder there is a `docker-compose.yml` that will start everything the application needs to run.
Just go to `DockerEnvOnly` and use

```
docker-compose up -d
```

and if all the ports are available, the following applications will start:

- Postgresql: to store donuts and ingredients
- Adminer: db managing browser app, just go to `http://localhost:8081/`
- MongoDB: to store orders and customer reviews
- MongoExpress: browser app to manage mongo, go to `http://localhost:8888/`

All credentials can be found in the `docker-compose.yml` file

### Docker Complete Environment (including application)

The application itself can be containerized by using the provided `Dockerfile`.
In the main folder of the project use:

```
docker build -t crunchymuesli/donut-shop .
```
to build an image for the application (it will take a while). Then, go to the `DockerCompleteEnv` folder, which contains another `docker-compose.yml`.
The difference from the other one is, this one includes the `donut-shop` image just created. Now use:
```
docker-compose up -d
```
to run the whole environment, application included. 


# Rest APIs

The application exposes the `swagger.json` [here](http://localhost:8080/donut-shop/v2/api-docs) and the `swagger-ui` [here](http://localhost:8080/donut-shop/swagger-ui/).  
The credentials to access the `admin` API can be found in `donut-shop-server/src/main/resources/application.properties`.

# FAQ

Was I hungry when I started this project? Yes.
