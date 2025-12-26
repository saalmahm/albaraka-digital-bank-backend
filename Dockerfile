# Étape 1 : build Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests clean package

# Étape 2 : image de runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Variables d’environnement (valeurs par défaut)
ENV JWT_SECRET=CHANGE_ME_USE_A_LONG_RANDOM_SECRET_KEY \
    DB_URL=jdbc:postgresql://db:5432/albaraka_db \
    DB_USER=postgres \
    DB_PASSWORD=salmahm \
    SPRING_PROFILES_ACTIVE=docker

# Copier le jar construit
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]